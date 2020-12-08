package com.xck.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 2020-12-08 测试数组和链表实现的数据
 * 机器：windows，4核，4G
 * 场景：2条生产，2条消费，每条生产50w数据，测试200次，都是有界1000
 * ArrayBlockingQueue 77ms
 * LinkedBlockingQueue 170ms
 * 场景：1条生产，1条消费，每条生产50w数据，测试200次，都是有界1000
 * ArrayBlockingQueue 60ms
 * LinkedBlockingQueue 68ms
 * 场景：1条生产，1条消费，每条生产50w数据，测试200次，链表实现无界
 * ArrayBlockingQueue 55ms
 * LinkedBlockingQueue 45ms
 *
 * 机器：linux，40核，60G
 * 场景：10条生产，10条消费，每条生产50w数据，测试200次，都是有界1000，堆参数无指定
 * ArrayBlockingQueue 2080ms
 * LinkedBlockingQueue 1386ms
 * 场景：20条生产，20条消费，每条生产50w数据，测试200次，都是有界1000，堆参数无指定
 * ArrayBlockingQueue 5656ms，9s一次垃圾回收，cpu300%左右
 * LinkedBlockingQueue 2680ms，2s一次垃圾回收，基本上每秒一次，到后面变成每秒2次
 *
 * 性能链表要好
 *
 */
public class ArrayAndLinkBlockingQueuePerformanceTest {

    public static CountDownLatch countDownLatch = new CountDownLatch(2);
    public static CountDownLatch isFinish = new CountDownLatch(2);
    public static CountDownLatch isTakeFinish = new CountDownLatch(2);
    public static BlockingQueue queue1 = null;

    public static void main(String[] args) throws InterruptedException{
        System.out.println("test ArrayBlockingQueue...");
        queue1 = new ArrayBlockingQueue(1000);
        long t = 0L;
        for(int i=0; i<200; i++){
            t+=test(1);
        }
        System.out.println(t/200);

        System.out.println("test LinkedBlockingQueue...");
        queue1 = new LinkedBlockingQueue();
        t = 0L;
        for(int i=0; i<200; i++){
            t+=test(1);
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
        while (queue1.size() > 0) {
            Thread.sleep(1);
        }
        for(Thread thread : takTArr){
            thread.interrupt();
        }
        isTakeFinish.await();

//        System.out.println(times + " " + (System.currentTimeMillis()-start));
        return time;
    }

    public static class TakeTask implements Runnable{

        @Override
        public void run() {
            countDownLatch.countDown();
            try {
                countDownLatch.await();
                while (!Thread.currentThread().isInterrupted()){
                    Object o = queue1.take();
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
                    ++count;
                }
            } catch (InterruptedException e) {
            }
            isFinish.countDown();
        }
    }
}
