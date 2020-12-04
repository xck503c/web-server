package com.xck.check.reactor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskPutBlockPolicy implements RejectedExecutionHandler {

    public BlockingQueue<Runnable> workQueue;

    public TaskPutBlockPolicy(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if(!executor.isShutdown()){
            try {
                workQueue.put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
