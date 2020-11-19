package com.xck.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class ArrayBlockQueueTest {

    public static CountDownLatch countDownLatch = new CountDownLatch(2);
    public static CountDownLatch isFinish = new CountDownLatch(2);
    public static CountDownLatch isTakeFinish = new CountDownLatch(2);
    public final static ArrayBlockingQueue queue1 = new ArrayBlockingQueue(1000);
//    public final static LinkedBlockingQueue queue1 = new LinkedBlockingQueue(1000);

    public static void main(String[] args) throws InterruptedException{
        System.out.println(Runtime.getRuntime().availableProcessors());
        long t = 0L;
        for(int i=0; i<200; i++){
            t+=test(i);
        }
        System.out.println(t);
    }

    public static long test(int times) throws InterruptedException{
        countDownLatch = new CountDownLatch(4);
        isFinish = new CountDownLatch(2);
        isTakeFinish = new CountDownLatch(2);
        Thread[] takTArr = new Thread[2];
        for(int i=0; i<2; i++){
            takTArr[i] = new Thread(new TakeTask());
            takTArr[i].start();
        }

        for(int i=0; i<2; i++){
            Thread t1 = new Thread(new PutTask());
            t1.start();
        }

        countDownLatch.await();

        long start = System.currentTimeMillis();
        isFinish.await();

        long time = System.currentTimeMillis()-start;
        while (queue1.size() > 0) {
            Thread.sleep(1);
        }
        for(Thread thread : takTArr){
            thread.interrupt();
        }
        isTakeFinish.await();

        System.out.println(times + " " + (System.currentTimeMillis()-start));
        return time;
    }

    public static class TakeTask implements Runnable{

        @Override
        public void run() {
            int count = 0;
            countDownLatch.countDown();
            try {
                countDownLatch.await();
                while (!Thread.currentThread().isInterrupted()){
                    Object o = queue1.take();
                    if(o!=null) count++;
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
                    queue1.put(count);
                    count++;
                }
            } catch (InterruptedException e) {
            }
            isFinish.countDown();
        }
    }
}
