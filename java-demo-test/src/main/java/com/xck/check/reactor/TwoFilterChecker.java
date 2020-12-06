package com.xck.check.reactor;


public class TwoFilterChecker implements CheckHandler {

    @Override
    public void handle(CheckEvent event) throws InterruptedException {
        Thread.sleep(2);
        final CheckHandler checkHandler = CheckDispatcher.checkHandlerMap.get(event.nextEvent());
        if(checkHandler == null){
            return;
        }
        checkHandler.handle(event);
    }
}
