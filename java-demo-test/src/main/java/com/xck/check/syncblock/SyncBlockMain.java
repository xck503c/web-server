package com.xck.check.syncblock;

import com.xck.check.policy.*;

import java.util.concurrent.*;

/**
 * @Classname SyncBlockMain
 * @Description TODO
 * @Date 2020/12/5 22:42
 * @Created by xck503c
 *
 * 同步阻塞方案，审核的时间=链上审核的时间总和
 */
public class SyncBlockMain {

    private final static ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(200);
    private static ThreadPoolExecutor executorService
            = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            60L, TimeUnit.SECONDS, workQueue, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                workQueue.put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public static void main(String[] args) {
        long diff = 0L;
        for(int j=0; j<3; j++){
            long start = System.currentTimeMillis();
            for(int i=0; i<20000; i++){
                final boolean is = i%2==0;
                final ItemCheckChain chain = assembleAndCheck(new Item(), is);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        ICheck iCheck = null;
                        while ((iCheck = chain.getChecker())!=null){
                            iCheck.doCheck(chain.getItem());
                        }
                    }
                });
            }
            long time = (System.currentTimeMillis()-start);
            diff+=time;
            while (executorService.getQueue().size() > 0){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("耗时: " + time);
        }
        executorService.shutdown();
        System.out.println("均值: " + (diff/3)); //1w-8292ms 2w-1680ms
    }

    private static ItemCheckChain assembleAndCheck(Item item, boolean isTakeUpTime){
        ItemCheckChain itemCheckChain = new ItemCheckChain(item);
        if (isTakeUpTime){
            itemCheckChain.add(OneCheck.getInstance());
            itemCheckChain.add(TwoCheck.getInstance());
            itemCheckChain.add(SubmitReapetCheck.getInstance());
        }else {
            itemCheckChain.add(OneCheck.getInstance());
            itemCheckChain.add(TwoCheck.getInstance());
        }

        return itemCheckChain;
    }
}
