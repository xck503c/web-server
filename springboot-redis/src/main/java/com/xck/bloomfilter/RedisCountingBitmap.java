package com.xck.bloomfilter;

import com.xck.redis.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;

public class RedisCountingBitmap extends RedisBitmap{

    public final long MB512 = 512*8*1024*1024L;

    private int counterBitSize;
    private long maxBit;

    private RedisPool redisPool = new RedisPool();

    public RedisCountingBitmap(String redisKey, int counterBitSize){
        super(redisKey);
        this.counterBitSize = counterBitSize;
    }

    //批量设置位图中的位
    public boolean set(List<Long> offsets){
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                Pipeline pipeline = jedis.pipelined();
                //先获取所有位
                List<Response<Boolean>> offsetCounter = new ArrayList<Response<Boolean>>();
                for(Long offset : offsets){
                    offsetCounter.add(pipeline.getbit(getRedisKey(offset), offset));
                }

                List<Response<Boolean>> result = new ArrayList<Response<Boolean>>(offsets.size());
                for(Long offset : offsets){
                    result.add(pipeline.setbit(getRedisKey(), offset, true));
                }
                pipeline.sync();
                if(result!=null && result.size()>0){
                    for(int i=0; i<result.size(); i++){
                        if(!result.get(i).get()){
                            return false;
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            redisPool.returnJedis(jedis);
        }
        return false;
    }

    //批量获取位图中的位
    public boolean isExists(long[] offsets){
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                Pipeline pipeline = jedis.pipelined();
                List<Response<Boolean>> result = new ArrayList<Response<Boolean>>(counterBitSize*offsets.length);
                for(int i=0; i<offsets.length; i++){
                    setPipeBitsInCounter(pipeline, offsets[i], result);
                }
                pipeline.sync();
                if(result!=null && result.size()>0){
                    int count = 0;

                    for(int i=0; i<result.size(); i++){
                        if(result.get(i).get()){

                        }
                    }
                    if(count == result.size()) return false;
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            redisPool.returnJedis(jedis);
        }
        return false;
    }

    public boolean set(long offset) {
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                return jedis.setbit(redisKey, offset, true);
            }
        } finally {
            redisPool.returnJedis(jedis);
        }
        return false;
    }

    public boolean isExist(long offset) {
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                return jedis.getbit(redisKey, offset);
            }
        } finally {
            redisPool.returnJedis(jedis);
        }
        return false;
    }

    public long bitCount(){
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                return jedis.bitcount(redisKey);
            }
        } finally {
            redisPool.returnJedis(jedis);
        }
        return -1;
    }

    //获取计数器中的位
    public void setPipeBitsInCounter(Pipeline pipeline, long offset, List<Response<Boolean>> result) {
        if(pipeline == null || offset >= maxbitSize){
            return;
        }

        //0001 0010 0100 1000
        //3 - 1000
        //4*3=12
        long firstBit = counterBitSize*offset;
        for(int i=0; i<counterBitSize; i++){
            long index = getKeyIndex(firstBit+i);
            result.add(pipeline.getbit(getRedisKey(index), firstBit+i));
        }
    }

    public String getRedisKey(long index){
        return redisKey + ":" + index;
    }

    public void setKeySize() {
        maxBit = getMaxbitSize()*counterBitSize;
        if(maxBit > MB512){
            this.keySize = maxBit/MB512+1;
            System.out.println("超过MB512的限制，需要扩展，key个数为：" + keySize);
        }else {
            this.keySize = 0;
        }
    }
}
