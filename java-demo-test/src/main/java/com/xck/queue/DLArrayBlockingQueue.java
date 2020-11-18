package com.xck.queue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DLArrayBlockingQueue extends VolatileLongPadding{


    private final Object[] items;
    private final int capacity;

//    private AtomicInteger count = new AtomicInteger();

    private final ReentrantLock takelock = new ReentrantLock();
    private final Condition notEmpty = takelock.newCondition();

    private final ReentrantLock putlock = new ReentrantLock();
    private final Condition notFull = putlock.newCondition();

    private final Thread signal = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    if(putParkCount == 0 && taskParkCount == 0){
                        Thread.sleep(50);
                        continue;
                    }

                    if (!isFull() && putParkCount >0) {
                        putlock.lockInterruptibly();
                        try {
                            if (!isFull() && putParkCount >0) {
                                notFull.signalAll();
                            }
                        } finally {
                            putlock.unlock();
                        }
                    }

                    if (!isEmpty() && taskParkCount > 0) {
                        takelock.lockInterruptibly();
                        try {
                            if (!isEmpty() && taskParkCount > 0) {
                                notEmpty.signalAll();
                            }
                        } finally {
                            takelock.unlock();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public DLArrayBlockingQueue(int size){
        this.capacity = size;
        this.items = new Object[capacity];
        signal.setDaemon(true);
        signal.start();
    }

    public void put(Object e) throws InterruptedException{
        putlock.lockInterruptibly();
        try {
            while (isFull()) {
                ++putParkCount;
//                System.out.println(Thread.currentThread().getName() + " put blocking isFull="+isEmpty() + " " + takeIndex + "-" + putIndex);
                notFull.await();
//                System.out.println(Thread.currentThread().getName() + " put no blocking");
                --putParkCount;
            }
            putIndex = (++putIndex == items.length) ? 0 : putIndex;
            items[putIndex] = e;
//            count.incrementAndGet();
        } finally {
            if(!isFull() && putParkCount > 0){
                notFull.signal();
            }
            putlock.unlock();
        }
    }

    public Object take() throws InterruptedException{
        Object o = null;
        takelock.lockInterruptibly();
        try {
//            long na = System.nanoTime();
//            int size = 0;
            while (isEmpty()){
//                ++size;
                ++taskParkCount;
//                System.out.println(Thread.currentThread().getName() + " take blocking isEmpty="+isEmpty() + " " + takeIndex + "-" + putIndex);
                notEmpty.await();
//                System.out.println(Thread.currentThread().getName() + " take not blocking");
                --taskParkCount;
            }
//            if (System.nanoTime() - na>1000) {
//                System.out.println((System.nanoTime() - na) + " " + size);
//            }
            takeIndex = (++takeIndex == items.length) ? 0 : takeIndex;
            o = items[takeIndex];
//            items[takeIndex] = null;
//            count.decrementAndGet();
        } finally {
            if(!isEmpty() && taskParkCount > 0){
                notEmpty.signal();
            }
            takelock.unlock();
        }

        return o;
    }

    public boolean isEmpty(){
//        return count.get() == 0;
        return putIndex == takeIndex;
    }

    public boolean isFull(){
        int putIndexCopy = putIndex;
        int put = (putIndexCopy+1 == items.length) ? 0 : putIndexCopy+1;
        return put == takeIndex;
    }

    public int getCount(){
        int put = putIndex;
        int take = takeIndex;
        if(put >= take){
            return put-take;
        }
        return items.length - takeIndex + putIndex+1;
    }

    public static CountDownLatch countDownLatch = new CountDownLatch(6);
    public static AtomicInteger isFinish = new AtomicInteger();
    public final static DLArrayBlockingQueue queue = new DLArrayBlockingQueue(1000);

    public static void main(String[] args) throws InterruptedException{
        System.out.println(456438+532130+507504+487195+482558+534175);
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
            if (queue.getCount() == 0) {
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
//                    System.out.println(Thread.currentThread().getName() + " take " + o + " " + queue.getCount());
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
//                    System.out.println(Thread.currentThread().getName() + " put " + count + " " + queue.getCount());
                    count++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isFinish.incrementAndGet();
            System.out.println("111" + Thread.currentThread().getName() + " put " + count + " " + queue.getCount());
        }
    }
}
