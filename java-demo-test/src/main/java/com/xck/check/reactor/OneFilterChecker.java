package com.xck.check.reactor;

public class OneFilterChecker implements CheckHandler {

    @Override
    public void handle(CheckEvent event) throws InterruptedException {
        Thread.sleep(1);
        final CheckHandler checkHandler = CheckDispatcher.checkHandlerMap.get(event.nextEvent());
        if(checkHandler == null){
            return;
        }
        checkHandler.handle(event);
    }

}
