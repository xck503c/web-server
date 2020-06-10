package com.xck.concurrent.statistics.demo1;

import com.xck.concurrent.statistics.ShareDataCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MainTest {

    public final static CountDownLatch countDownLatch = new CountDownLatch(12);
    public final static CountDownLatch startLatch = new CountDownLatch(12);

    public static void main(String[] args) throws Exception{
        List<Thread> list = new ArrayList<Thread>();

        for(int i = 0; i<6; i++){
            list.add(new Thread(new ProduceStatisticsTask("test1")));
        }
        for(int i = 0; i<6; i++){
            list.add(new Thread(new ProduceStatisticsTask("test2")));
        }
//        for(int i = 0; i<3; i++){
//            list.add(new Thread(new ProduceStatisticsTask("test3")));
//        }

        new Thread(new Runnable() {

            Map<String, Integer> sumMap = new HashMap<String, Integer>();

            public void run() {
                try {
                    while (true) {
                        Map<String, AtomicInteger> map = ShareDataCenter.getStatistics();
                        if(map == null){
                            if(countDownLatch.getCount() == 0){
                                break;
                            }
                            Thread.sleep(100);
                            continue;
                        }
                        for(String key : map.keySet()){
                            Integer i = sumMap.get(key);
                            if(i == null) {
                                sumMap.put(key, map.get(key).get());
                                continue;
                            }

                            sumMap.put(key, i += map.get(key).get());
                        }
                    }
                    System.out.println(sumMap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        for(int i = 0; i<12; i++){
            list.get(i).start();
        }

        countDownLatch.await();
    }

    public static class ProduceStatisticsTask implements Runnable{
        private String key;

        public ProduceStatisticsTask(String key){
            this.key = key;
        }

        public void run(){
            startLatch.countDown();
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long start = System.currentTimeMillis();
            for (int i = 0; i< 10000; i++) {
                ShareDataCenter.accumulate(key, 1);
            }
            countDownLatch.countDown();
            System.out.println(Thread.currentThread().getName() + "-" + (System.currentTimeMillis()-start));
        }
    }
}
