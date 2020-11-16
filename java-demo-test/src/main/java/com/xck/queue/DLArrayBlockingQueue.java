package com.xck.queue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DLArrayBlockingQueue {

    private int putIndex;
    private int takeIndex;

    private volatile int taskParkCount;
    private volatile int putParkCount;

    private final Object[] items;

    private AtomicInteger count = new AtomicInteger();

    private final ReentrantLock takelock = new ReentrantLock(true);
    private final Condition notEmpty = takelock.newCondition();

    private final ReentrantLock putlock = new ReentrantLock(true);
    private final Condition notFull = putlock.newCondition();

    public DLArrayBlockingQueue(int size){
        this.items = new Object[size];
    }

    public void put(Object e) throws InterruptedException{
        putlock.lockInterruptibly();
        try {
            while (count.get() == items.length) {
                ++putParkCount;
                notFull.await();
                --putParkCount;
            }
            items[putIndex] = e;
            putIndex = (++putIndex == items.length) ? 0 : putIndex;
            count.incrementAndGet();
            if (taskParkCount > 0) {
                takelock.lockInterruptibly();
                try {
                    if (taskParkCount > 0) {
                        notEmpty.signal();
                    }
                } finally {
                    takelock.unlock();
                }
            }
        } finally {
            putlock.unlock();
        }
    }

    public Object take() throws InterruptedException{
        Object o = null;
        takelock.lockInterruptibly();
        try {
            while (count.get() == 0){
                ++taskParkCount;
                notEmpty.await();
                --taskParkCount;
            }
            o = items[takeIndex];
            items[takeIndex] = null;
            takeIndex = (++takeIndex == items.length) ? 0 : takeIndex;
            count.decrementAndGet();
            if (putParkCount > 0) {
                putlock.lockInterruptibly();
                try {
                    if (putParkCount > 0) {
                        notFull.signal();
                    }
                } finally {
                    putlock.unlock();
                }
            }
        } finally {
            takelock.unlock();
        }

        return o;
    }

    public static CountDownLatch countDownLatch = new CountDownLatch(6);
    public static AtomicInteger isFinish = new AtomicInteger();
    public final static DLArrayBlockingQueue queue = new DLArrayBlockingQueue(1000);

    public static void main(String[] args) throws InterruptedException{
        Thread[] takTArr = new Thread[6];
        for(int i=0; i<6; i++){
            takTArr[i] = new Thread(new TakeTask());
            takTArr[i].start();
        }

        for(int i=0; i<6; i++){
            Thread t1 = new Thread(new PutTask());
            t1.start();
        }

        countDownLatch.await();

        long start = System.currentTimeMillis();

        while (isFinish.get()<6){
            Thread.sleep(10);
        }

        while (isFinish.get()<12){
            if (queue.count.get() == 0) {
                for(Thread thread : takTArr){
                    thread.interrupt();
                }
            }
            Thread.sleep(10);
        }

        System.out.println(isFinish + " " + (System.currentTimeMillis()-start));
    }

    public static class TakeTask implements Runnable{

        @Override
        public void run() {
            int count = 0;
            try {
                while (!Thread.currentThread().isInterrupted()){
                    Object o = queue.take();
                    if(o!=null) count++;
                    System.out.println(Thread.currentThread().getName() + " take " + o);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isFinish.incrementAndGet();
            System.out.println("111" + Thread.currentThread().getName() + " take " + count);
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
                    System.out.println(Thread.currentThread().getName() + " put " + count + " " + queue.count.get());
                    count++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isFinish.incrementAndGet();
            System.out.println("111" + Thread.currentThread().getName() + " put " + count + " " + queue.count.get());
        }
    }
}
