package com.xck.check.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class CheckHandler extends Thread{

    private transient volatile boolean isRunning = true;

    protected transient BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
    protected transient ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
            , Runtime.getRuntime().availableProcessors()+2, 60
            , TimeUnit.SECONDS, workQueue, new TaskPutBlockPolicy(workQueue));

    protected BlockingQueue<CheckEvent> queue;

    public CheckHandler() {
        this.queue = new ArrayBlockingQueue<>(5000);
    }

    public CheckHandler(int size) {
        this.queue = new ArrayBlockingQueue<>(size);
    }

    public void handle(CheckEvent event) throws InterruptedException{
        queue.put(event);
    }

    @Override
    public void run(){
        while (isRunning){
            try {
                if(queue.size() > 0){
                    if(!pool.isShutdown()){
                        pool.submit(new AbstractCheckTask());
                    }
                }else {
                    sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract boolean detailTask() throws InterruptedException;

    public class AbstractCheckTask implements Runnable{

        @Override
        public void run() {
            boolean isAdd = false;
            try{
                isAdd = detailTask();
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                if(isAdd){
                    workQueue.offer(this);
                }
            }
        }
    }

    public List<CheckEvent> batchDeal(){
        List<CheckEvent> list = new ArrayList<>();
        for(int i=0; i<1000; i++){
            CheckEvent checkEvent = queue.poll();
            if(checkEvent == null) continue;
            list.add(checkEvent);
        }
        return list;
    }
}
