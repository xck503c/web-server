package com.xck.queue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 2020-11-19 截止，照搬LinkedBlockingQueue中的双锁方式，实现后性能和LinkedBlockingQueue差不多
 * 但是耗时比ArrayBlockingQueue久
 * 测试场景：2生产，2消费，每个生产50w整型数据，队列长度为1000
 *
 * 2020-12-08 换了环境重新测试
 * 测试场景：20生产，20消费，每个生产50w数据，队列长度1000
 * 机器参数：可用核心数40，内存管够，启动不指定参数
 * 测试结果：2253ms，cpu略高350%左右，ygc比数组单锁实现要频繁，比链表实现要好
 */
public class DLArrayBlockingQueue{

    public int putIndex;
    public int takeIndex;

    private final Object[] items;
    private final int capacity;

    private AtomicInteger count = new AtomicInteger();

    private final ReentrantLock takelock = new ReentrantLock();
    private final Condition notEmpty = takelock.newCondition();

    private final ReentrantLock putlock = new ReentrantLock();
    private final Condition notFull = putlock.newCondition();

    public DLArrayBlockingQueue(int size){
        this.capacity = size;
        this.items = new Object[capacity];
    }

    public void put(Object e) throws InterruptedException{
        int c = -1;
        putlock.lockInterruptibly();
        try {
            while (isFull()) {
                notFull.await();
            }
            items[putIndex] = e;
            putIndex = (++putIndex == items.length) ? 0 : putIndex;
            c = count.getAndIncrement();
        } finally {
            if(c+1 < capacity){
                notFull.signal();
            }
            putlock.unlock();
        }
        if(c == 0){
            takelock.lockInterruptibly();
            try {
                notEmpty.signal();
            } finally {
                takelock.unlock();
            }
        }
    }

    public Object take() throws InterruptedException{
        Object o = null;
        int c = -1;
        takelock.lockInterruptibly();
        try {
            while (isEmpty()){
                notEmpty.await();
            }
            o = items[takeIndex];
            items[takeIndex] = null;
            takeIndex = (++takeIndex == items.length) ? 0 : takeIndex;
            c = count.getAndDecrement();
        } finally {
            if(c > 1){
                notEmpty.signal();
            }
            takelock.unlock();
        }
        if(c == capacity){
            putlock.lockInterruptibly();
            try {
                notFull.signal();
            } finally {
                putlock.unlock();
            }
        }

        return o;
    }

    public boolean isEmpty(){
        return count.get() == 0;
    }

    public boolean isFull(){
        return count.get() == capacity;
    }

    public int getCount(){
        return count.get();
    }

    public static CountDownLatch countDownLatch = new CountDownLatch(2);
    public static CountDownLatch isFinish = new CountDownLatch(2);
    public static CountDownLatch isTakeFinish = new CountDownLatch(2);
    public static DLArrayBlockingQueue queue = new DLArrayBlockingQueue(1000);

    public static void main(String[] args) throws InterruptedException{
        long t = 0L;
        for(int i=0; i<200; i++){
            t+=test(2);
        }
        System.out.println(t/200);
    }

    public static long test(int threadSize) throws InterruptedException{
        countDownLatch = new CountDownLatch(threadSize*2);
        isFinish = new CountDownLatch(threadSize);
        isTakeFinish = new CountDownLatch(threadSize);
        Thread[] takTArr = new Thread[threadSize];
        for(int i=0; i<threadSize; i++){
            takTArr[i] = new Thread(new TakeTask());
            takTArr[i].start();
        }

        for(int i=0; i<threadSize; i++){
            Thread t1 = new Thread(new PutTask());
            t1.start();
        }

        countDownLatch.await();

        long start = System.currentTimeMillis();
        isFinish.await();

        long time = System.currentTimeMillis()-start;

        while (queue.getCount() > 0) {
            Thread.sleep(1);
        }
        for(Thread thread : takTArr){
            thread.interrupt();
        }
        isTakeFinish.await();

        return time;
    }

    public static class TakeTask implements Runnable{

        @Override
        public void run() {
            countDownLatch.countDown();
            try {
                countDownLatch.await();
                while (!Thread.currentThread().isInterrupted()){
                    Object o = queue.take();
                }
            } catch (InterruptedException e) {
            }
            isTakeFinish.countDown();
        }
    }

    public static class PutTask implements Runnable{

        @Override
        public void run() {
            int count = 0;
            countDownLatch.countDown();
            try {
                countDownLatch.await();
                while (count < 500000){
                    queue.put(count);
                    count++;
                }
            } catch (InterruptedException e) {
            }
            isFinish.countDown();
        }
    }
}
