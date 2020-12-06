package com.xck.check.reactor;

/**
 * @Classname NoneChecker
 * @Description TODO
 * @Date 2020/12/6 19:51
 * @Created by xck503c
 */
public class NoneChecker implements CheckHandler {

    @Override
    public void handle(CheckEvent event) throws InterruptedException {
        CheckDispatcher.atomicInteger.incrementAndGet();
    }
}
