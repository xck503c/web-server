package com.xck.jdk.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Spliterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpliteratorTest {

    public static void main(String[] args) throws Exception{
        parallelSum();
    }

    public static void trySplitTest(){
        final AtomicInteger i = new AtomicInteger(0);
        List<Integer> list = Stream.iterate(0, x->x+1).limit(10).collect(Collectors.toList());
        PriorityQueue priorityQueue = new PriorityQueue(list);

        Spliterator spliterator = priorityQueue.spliterator();
        Spliterator spliterator1 = spliterator.trySplit();
        System.out.println(spliterator.estimateSize());
        System.out.println(spliterator1.estimateSize());
    }

    public static void parallelSum() throws Exception{
        List<Integer> list = Stream.iterate(1, x->x+1).limit(100).collect(Collectors.toList());
        Spliterator spilt = list.spliterator();
        int cpus = Runtime.getRuntime().availableProcessors();

        AtomicInteger count = new AtomicInteger(0);

        List<Spliterator> task = new ArrayList<>();
        for(int i=0; i<cpus; i++){
            Spliterator tmp = spilt.trySplit();
            task.add(tmp);
        }
        task.add(spilt);

        CountDownLatch end = new CountDownLatch(task.size());

        for(int i=0; i<task.size(); i++){
            final int index = i;
            Thread t = new Thread(() -> {
                task.get(index).forEachRemaining((o) -> {
                    count.addAndGet(Integer.parseInt(o+""));
                });
                end.countDown();
            });
            t.start();
        }
        end.await();
        System.out.println(count.get());
    }
}
