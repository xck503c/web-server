package com.xck.concurrent.statistics;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsModel {

    private AtomicInteger sendCount = new AtomicInteger(0);

    public void addSendCount(int value){
        sendCount.getAndAdd(value);
    }
}
