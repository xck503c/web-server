package com.xck.queue;

public class VolatileLongPadding {
    public int putIndex=1;
    public int putParkCount;
    public volatile long p1, p2, p3, p4, p5, p6; // 注释
    public int taskParkCount;
    public int takeIndex;
}
