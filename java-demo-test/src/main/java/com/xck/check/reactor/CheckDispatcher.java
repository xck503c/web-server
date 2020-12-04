package com.xck.check.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class CheckDispatcher extends Thread{

    @Override
    public void run() {
        while (true) {
            try {
                List<CheckEvent> list = batchDeal();
                if(list.isEmpty()){
                    Thread.sleep(100);
                    continue;
                }

                for(CheckEvent event : list){
                    CheckHandler checkHandler = DataCheckCenter.checkHandlerMap.get(event.nextEvent());
                    checkHandler.handle(event);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<CheckEvent> batchDeal(){
        List<CheckEvent> list = new ArrayList<>();
        ArrayBlockingQueue<CheckEvent> queue = DataCheckCenter.checkEventQueue;
        for(int i=0; i<1000; i++){
            CheckEvent checkEvent = queue.poll();
            if(checkEvent == null) continue;
            list.add(checkEvent);
        }
        return list;
    }
}
