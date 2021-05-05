package com.xck.bdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 对比两种阻塞队列的性能
 */
public class BlockingQueuePerformanceTest {

    public static void main(String[] args) throws InterruptedException{
        MultiVABMessagePCNoEQQueueTest();
    }

    public static void MultiVABMessagePCNoEQQueueTest() throws InterruptedException{
        testBase(8000, 2, 1);
        testBase(8000, 4, 1);
        testBase(8000, 4, 2);
        testBase(8000, 8, 4);

        testBase(8000, 1, 2);
        testBase(8000, 1, 4);
        testBase(8000, 1, 6);
        testBase(8000, 1, 8);
        testBase(8000, 2, 4);
    }

    public static CountDownLatch countDownLatch = null;
    public static CountDownLatch isFinish = null;
    public static CountDownLatch isTakeFinish =null;
    public static BdbPersistentQueue<ArrayList> queue1 = null;

    public static void testBase(int capacity, int producerSize, int consumerSize) throws InterruptedException{
        String path = System.getProperty("user.dir");
        queue1 = new BdbPersistentQueue<>(path + "/mq/bdb", "test", ArrayList.class);
        long t = 0L;
        for(int i=0; i<200; i++){
            t+=test(producerSize, consumerSize);
        }
        System.out.println("end: " + t/200);
    }

    public static long test(int producerSize, int consumerSize) throws InterruptedException{
        countDownLatch = new CountDownLatch(producerSize + consumerSize);
        isFinish = new CountDownLatch(producerSize);
        isTakeFinish = new CountDownLatch(consumerSize);
        Thread[] takTArr = new Thread[consumerSize];
        for(int i=0; i<consumerSize; i++){
            takTArr[i] = new Thread(new TakeTask());
            takTArr[i].start();
        }

        for(int i=0; i<producerSize; i++){
            Thread t1 = new Thread(new PutTask());
            t1.start();
        }

        countDownLatch.await();

        long start = System.currentTimeMillis();
        System.out.println("wait");
        isFinish.await();

        long time = System.currentTimeMillis()-start;
        while (queue1.size() > 0) {
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
                    Object o = queue1.poll();
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
                ArrayList<String> list = new ArrayList<>(500);
                while (count < 800000){
                    list.add(count+"");
                    ++count;
                    if(list.size() >= 500){
                        queue1.offer(list);
                        list.clear();
                    }
                }
            } catch (InterruptedException e) {
            }
            isFinish.countDown();
        }
    }
}
