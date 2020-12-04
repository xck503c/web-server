package com.xck.check.reactor;

import com.sun.jdi.event.Event;

import java.util.List;

public class OtherFilterChecker extends CheckHandler{

    @Override
    public boolean detailTask() throws InterruptedException{
        List<CheckEvent> list = batchDeal();
        if(list.isEmpty()) return false;

        for(CheckEvent event : list){
            DataCheckCenter.checkEventQueue.put(event);
        }

        return true;
    }
}
