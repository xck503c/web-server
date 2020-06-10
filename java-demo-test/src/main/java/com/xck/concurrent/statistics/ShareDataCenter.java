package com.xck.concurrent.statistics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShareDataCenter {

    public static ConcurrentHashMap<String, AtomicInteger> statistics
            = new ConcurrentHashMap<String, AtomicInteger>();

    public final static ReadWriteLock lock = new ReentrantReadWriteLock();
    public final static Lock reentrantLock = new ReentrantLock();


    public static void accumulate(String key, Integer value){
        try {
//            lock.readLock().lock();
            reentrantLock.lock();
            AtomicInteger oldModel = statistics.get(key);
            if(oldModel == null){
                oldModel = new AtomicInteger(0);
                oldModel.getAndAdd(value);

                oldModel = statistics.putIfAbsent(key, oldModel);
                if(oldModel != null){
                    oldModel.getAndAdd(value);
                }
            }else {
                oldModel.getAndAdd(value);
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            reentrantLock.unlock();
//            lock.readLock().unlock();
        }
    }

    public static ConcurrentHashMap<String, AtomicInteger> getStatistics(){
        reentrantLock.lock();
//        lock.writeLock().lock();
        try {
            if(statistics.isEmpty()){
                return null;
            }
            ConcurrentHashMap<String, AtomicInteger> tmp = statistics;
            statistics = new ConcurrentHashMap<String, AtomicInteger>();
            return tmp;
        } finally {
            reentrantLock.unlock();
//            lock.writeLock().unlock();
        }
    }
}
