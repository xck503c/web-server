package com.xck.redis;

import org.redisson.Redisson;
import org.redisson.RedissonBlockingQueue;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

public class RedissonExtend extends Redisson {

    public RedissonExtend(Config config) {
        super(config);
    }

    public static RedissonClient create(Config config) {
        RedissonExtend redisson = new RedissonExtend(config);
        if (config.isReferenceEnabled()) {
            redisson.enableRedissonReferenceSupport();
        }
        return redisson;
    }

    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String name) {
        return new RedissonBlockingQueueExtend<V>(connectionManager.getCommandExecutor(), name, this);
    }

    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String name, Codec codec) {
        return new RedissonBlockingQueueExtend<V>(codec, connectionManager.getCommandExecutor(), name, this);
    }
}
