package com.xck.submithissave;

public class DataBackLog {
    private String storeHost;
    private long queueOffset = -1;
    private long maxoffset = -1;
    private long backlogNum = -1;

    public DataBackLog(String storeHost){
        this.storeHost = storeHost;
    }

    public void setOffset(long queueOffset, long maxoffset){
        this.queueOffset = queueOffset;
        this.maxoffset = maxoffset;
        backlogNum = maxoffset - queueOffset;
    }

    @Override
    public String toString() {
        return backlogNum + ", " + storeHost;
    }
}
