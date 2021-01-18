package com.xck.redis;

import org.redisson.RedissonBlockingQueue;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.connection.decoder.ListDrainToDecoder;
import org.redisson.misc.RedissonPromise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.redisson.client.protocol.RedisCommands.LPUSH_BOOLEAN;

public class RedissonBlockingQueueExtend<V> extends RedissonBlockingQueue<V> {

    public RedissonBlockingQueueExtend(CommandAsyncExecutor commandExecutor, String name, RedissonClient redisson) {
        super(commandExecutor, name, redisson);
    }

    public RedissonBlockingQueueExtend(Codec codec, CommandAsyncExecutor commandExecutor, String name, RedissonClient redisson) {
        super(codec, commandExecutor, name, redisson);
    }

    @Override
    public RFuture<Boolean> addAllAsync(final Collection<? extends V> c) {
        if (c.isEmpty()) {
            return RedissonPromise.newSucceededFuture(false);
        }

        List<Object> args = new ArrayList<Object>(c.size() + 1);
        args.add(getName());
        encode(args, c);
        return commandExecutor.writeAsync(getName(), codec, LPUSH_BOOLEAN, args.toArray());
    }

    @Override
    public RFuture<Integer> drainToAsync(Collection<? super V> c, int maxElements) {
        List<Object> keys = new ArrayList<>();
        keys.add(getName());
        //这里不能做为values因为内部会当成Integer，序列化为对象，太坑了。
//        keys.add(maxElements);

        return commandExecutor.evalWriteAsync(getName(), codec, new RedisCommand<Object>("EVAL", new ListDrainToDecoder(c)),
                "local elemNum = tonumber(ARGVS[1]);"
                        + "local vals = redis.call('lrange', KEYS[1], -elemNum, -1);"
                        + "redis.call('ltrim', KEYS[1], 0, -elemNum-1);"
                        + "return vals"
                , keys, maxElements);
    }
}
