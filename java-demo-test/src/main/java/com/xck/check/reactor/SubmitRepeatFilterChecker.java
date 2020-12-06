package com.xck.check.reactor;

import java.util.List;

public class SubmitRepeatFilterChecker extends PoolCheckHandler {

    @Override
    public boolean detailTask() throws InterruptedException{
        List<CheckEvent> list = batchDeal();
        if(list.isEmpty()) return false;


        for(CheckEvent event : list){
            Thread.sleep(6); //<=6ms 都可以消费过来
            CheckHandler checkHandler = CheckDispatcher.checkHandlerMap.get(event.nextEvent());
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

        return true;
    }
}
