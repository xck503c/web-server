package com.xck.redis;

import com.xck.config.RedisProperties;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname QueueTest
 * @Description TODO
 * @Date 2021/1/9 20:10
 * @Created by xck503c
 */
@Service
public class RedissonPool {
    private RedissonClient redissonClient = null;

    private RedisProperties redisProperties;

    public RedissonPool init(RedisProperties redisProperties){
        this.redisProperties = redisProperties;
        if ("single".equals(redisProperties.getMode())) {
            if(redissonClient == null) initSingle();
        }else if("sentinel".equals(redisProperties.getMode())){
            if(redissonClient == null) initSentinel();
        }
        System.out.println("init success!!!");
        return this;
    }

    public void close(){
        if ("single".equals(redisProperties.getMode())) {
            if(redissonClient != null) redissonClient.shutdown();
        }else if("sentinel".equals(redisProperties.getMode())){
            if(redissonClient == null) redissonClient.shutdown();
        }
    }

    private void initSingle(){
        try {
            if(redissonClient == null){
                Config config = new Config();
                config.useSingleServer()
                        .setAddress("redis://"+redisProperties.getUrl())
                        .setConnectionPoolSize(redisProperties.getConnectPoolSize())
                        .setConnectionMinimumIdleSize(redisProperties.getMinIdleSize())
                        .setConnectTimeout(redisProperties.getConnectTimeout())
                        .setTimeout(redisProperties.getTimeout());
                if(StringUtils.isNotBlank(redisProperties.getClientName())){
                    config.useSingleServer().setClientName(redisProperties.getClientName());
                }
                if(StringUtils.isNotBlank(redisProperties.getPwd())){
                    config.useSingleServer().setPassword(redisProperties.getPwd());
                }
//                config.setCodec(JsonJacksonCodec.INSTANCE);
                config.setCodec(JdkObjCodec.INSTANCE);
                redissonClient = RedissonExtend.create(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void initSentinel(){
        try {
            if(redissonClient == null){
                Config config = new Config();
                String[] sentinelIpArr = redisProperties.getSentinelUrl().split(";");
                for(String sentinelIp : sentinelIpArr){
                    config.useSentinelServers().addSentinelAddress("redis://" + sentinelIp);
                }
                config.useSentinelServers()
                        .setMasterConnectionPoolSize(redisProperties.getConnectPoolSize())
                        .setMasterConnectionMinimumIdleSize(redisProperties.getMinIdleSize())
                        .setSlaveConnectionPoolSize(redisProperties.getConnectPoolSize())
                        .setSlaveConnectionMinimumIdleSize(redisProperties.getMinIdleSize())
                        .setReadMode(ReadMode.SLAVE)
                        .setConnectTimeout(redisProperties.getConnectTimeout())
                        .setTimeout(redisProperties.getTimeout())
                        .setMasterName(redisProperties.getMasterName());
                if(StringUtils.isNotBlank(redisProperties.getClientName())){
                    config.useSentinelServers().setClientName(redisProperties.getClientName());
                }
                if(StringUtils.isNotBlank(redisProperties.getPwd())){
                    config.useSingleServer().setPassword(redisProperties.getPwd());
                }
                config.setCodec(JdkObjCodec.INSTANCE);
                redissonClient = RedissonExtend.create(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public RedissonClient getClient(){
        return redissonClient;
    }

    public Object getObject(String key){
        RBucket<Object> result = redissonClient.getBucket(key);
        return result.get();
    }

    public void setObject(String key, Object o){
        RBucket<Object> result = redissonClient.getBucket(key);
        result.set(o);
    }

    public void setString(String key, String o) throws UnsupportedEncodingException {
        RBinaryStream rBinaryStream = redissonClient.getBinaryStream(key);
        rBinaryStream.set(o.getBytes("utf-8"));
    }

    public <T> boolean enQueue(String key, List<T> list){
        RBlockingQueue<T> result = redissonClient.getBlockingQueue(key);
        return result.addAll(list);
    }

    public List<Object> outQueue(String key, int size){
        RBlockingQueue<Object> result = redissonClient.getBlockingQueue(key);
        List<Object> list = new ArrayList<>();
        result.drainTo(list, size);
        return list;
    }

    /**
     * 这里重新实现，批量的rpop操作，redisson自带的功能drainTo好像有点问题，会重复的rpush
     * @param key
     * @param size
     * @return
     */
    public List<Object> outQueueRPop(String key, int size){
        List<Object> list = new ArrayList<>();

        List<Object> keys = new ArrayList<>();
        keys.add(key);
        //这里不能做为values因为内部会当成Integer，序列化为对象，太坑了。
        keys.add(size);

        RScript rScript = redissonClient.getScript();
        ArrayList l = rScript.eval(RScript.Mode.READ_WRITE, "local elemNum = tonumber(KEYS[2]);"
                        + "local vals = redis.call('lrange', KEYS[1], -elemNum, -1);"
                        + "redis.call('ltrim', KEYS[1], 0, -elemNum-1);"
                        + "return vals"
                , RScript.ReturnType.MULTI, keys);
        if(l!=null && l.size()>0){
            list.addAll(l);
        }

        return list;
    }

    public int queueSize(String queueName){
        RBlockingQueue<Object> result = redissonClient.getBlockingQueue(queueName);
        return result.size();
    }

    public void sadd(String key, byte[] value){

    }

    public void scriptExc(String script, List<String> keys){
        RScript rScript = redissonClient.getScript();
//        rScript.eval(RScript.Mode.READ_WRITE, script, List<>);
    }

    public void lock(String key){
        RLock rLock = redissonClient.getLock(key);
        rLock.lock();

        try{

        }finally {
            rLock.unlock();
        }
    }
}
