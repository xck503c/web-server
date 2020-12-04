package com.xck.check.reactor;

import java.util.List;

public class SubmitRepeatFilterChecker extends CheckHandler{

    @Override
    public boolean detailTask() throws InterruptedException{
        List<CheckEvent> list = batchDeal();
        if(list.isEmpty()) return false;

        Thread.sleep(50);

        for(CheckEvent event : list){
            DataCheckCenter.checkEventQueue.put(event);
        }

        return true;
    }
}
