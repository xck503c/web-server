package com.xck.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayBlockQueueTest {

    public static CountDownLatch countDownLatch = new CountDownLatch(6);
    public static AtomicInteger isFinish = new AtomicInteger();
    public final static ArrayBlockingQueue queue1 = new ArrayBlockingQueue(1000);

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
            if (queue1.size() == 0) {
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
                    Object o = queue1.take();
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
                    queue1.put(count);
                    System.out.println(Thread.currentThread().getName() + " put " + count + " " + queue1.size());
                    count++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isFinish.incrementAndGet();
            System.out.println("111" + Thread.currentThread().getName() + " put " + count + " " + queue1.size());
        }
    }
}
