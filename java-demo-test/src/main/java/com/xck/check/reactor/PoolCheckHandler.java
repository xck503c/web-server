package com.xck.check.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件处理器基类，特点就是用线程池处理同类型的审核
 */
public abstract class PoolCheckHandler extends Thread implements CheckHandler{

    private volatile boolean isRunning = true;

    protected BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
    protected ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
            , Runtime.getRuntime().availableProcessors(), 60
            , TimeUnit.SECONDS, workQueue, new TaskPutBlockPolicy(workQueue));

    protected BlockingQueue<CheckEvent> queue;

    public PoolCheckHandler() {
        this.queue = new ArrayBlockingQueue<>(5000);
    }

    public PoolCheckHandler(int size) {
        this.queue = new ArrayBlockingQueue<>(size);
    }

    private static int i = 0;
    public void handle(CheckEvent event) throws InterruptedException{
        boolean result = queue.offer(event);
        if(!result){
            System.out.println("满了");
            queue.put(event);
        }
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
                    sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 任务的处理逻辑
     * @return
     * @throws InterruptedException
     */
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

    /**
     * 批量获取，给子类使用
     * @return
     */
    public List<CheckEvent> batchDeal(){
        List<CheckEvent> list = new ArrayList<>();
        for(int i=0; i<1; i++){
            CheckEvent checkEvent = queue.poll();
            if(checkEvent == null) continue;
            list.add(checkEvent);
        }
        return list;
    }

    public int queueSize(){
        return queue.size();
    }
}
