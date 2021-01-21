package com.xck.redisDistributeLock;

public interface WatchState {
    void taskRunningState();

    void lockWaitState();
}
