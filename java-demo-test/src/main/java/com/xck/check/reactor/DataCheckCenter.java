package com.xck.check.reactor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class DataCheckCenter {

    public static Map<EventType, CheckHandler> checkHandlerMap = new HashMap<>();

    public static ArrayBlockingQueue<CheckEvent> checkEventQueue = new ArrayBlockingQueue<>(5000);


}
