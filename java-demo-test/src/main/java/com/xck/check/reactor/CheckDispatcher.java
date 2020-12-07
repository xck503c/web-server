package com.xck.check.reactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckDispatcher extends Thread{

    public static Map<EventType, CheckHandler> checkHandlerMap = new HashMap<>();

    /**
     * 事件队列，需要审核的会被包装成CheckEvent，由审核分派器分派
     * 审核链上的节点处理完后不会放到事件队列上面，避免循环阻塞的情况出现，而是根据是否线程池实现进行处理
     */
    public static ArrayBlockingQueue<CheckEvent> checkEventQueue = new ArrayBlockingQueue<>(5000);

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
    public static ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
            , Runtime.getRuntime().availableProcessors(), 60
            , TimeUnit.SECONDS, workQueue, new TaskPutBlockPolicy(workQueue));

    static {
        final SubmitRepeatFilterChecker submitRepeatFilterChecker = new SubmitRepeatFilterChecker();
        submitRepeatFilterChecker.start();

        checkHandlerMap.put(EventType.none, new NoneChecker());
        checkHandlerMap.put(EventType.one, new OneFilterChecker());
        checkHandlerMap.put(EventType.two, new TwoFilterChecker());
        checkHandlerMap.put(EventType.submitRepeatFilter, submitRepeatFilterChecker);

        Thread monitor = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println("submitRepeat queue size: " + submitRepeatFilterChecker.queueSize());
                    System.out.println("event queue size: " + checkEventQueue.size());
                    System.out.println();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        monitor.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<CheckEvent> list = batchDeal();
                if(list.isEmpty()){
                    Thread.sleep(100);
                    continue;
                }

                for(final CheckEvent event : list){
                    final CheckHandler checkHandler = checkHandlerMap.get(event.nextEvent());
                    if(checkHandler == null){
                        continue;
                    }
                    if(!(checkHandler instanceof PoolCheckHandler)){
                        CheckDispatcher.pool.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    checkHandler.handle(event);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        checkHandler.handle(event);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void putEvent(CheckEvent checkEvent) throws InterruptedException{
        checkEventQueue.put(checkEvent);
    }

    /**
     * 事件分发
     * @return
     */
    public List<CheckEvent> batchDeal(){
        List<CheckEvent> list = new ArrayList<>();
        for(int i=0; i<1000; i++){
            CheckEvent checkEvent = checkEventQueue.poll();
            if(checkEvent == null) continue;
            list.add(checkEvent);
        }
        return list;
    }
}
