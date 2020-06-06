package com.xck.concurrent.statistics;

import java.util.concurrent.ConcurrentHashMap;

public class ShareDataCenter {

    private static ConcurrentHashMap<String, StatisticsModel> statistics
            = new ConcurrentHashMap<String, StatisticsModel>();


    public static void accumulate(String key, Integer value){
        StatisticsModel oldModel = statistics.get(key);
        if(oldModel == null){

        }
    }
}
