package com.xck.bloomfilter;

import com.xck.redis.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;

public class RedisBitmap implements Bitmap{

    public final long MB512 = 512*8*1024*1024L;

    //位图的key
    protected String redisKey;
    protected long maxbitSize;
    protected long keySize;

    private RedisPool redisPool = new RedisPool();

    public RedisBitmap(String redisKey){
        this.redisKey = redisKey;
        redisPool.init();
    }

    //批量设置位图中的位
    public boolean set(List<Long> offsets){
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                Pipeline pipeline = jedis.pipelined();
                List<Response<Boolean>> result = new ArrayList<Response<Boolean>>(offsets.size());
                for(long offset : offsets){
                    if (offset < 0 ) continue;
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
                List<Response<Boolean>> result = new ArrayList<Response<Boolean>>(offsets.length);
                for(int i=0; i<offsets.length; i++){
                    if (offsets[i] < 0 ) continue;
                    result.add(pipeline.getbit(getRedisKey(), offsets[i]));
                }
                pipeline.sync();
                if(result!=null && result.size()>0){
                    int count = 0;
                    for(int i=0; i<result.size(); i++){
                        if(result.get(i).get()){
                            ++count;
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

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public long getMaxbitSize() {
        return maxbitSize;
    }

    public void setMaxbitSize(long maxbitSize) {
        this.maxbitSize = maxbitSize;
    }

    public String getRedisKey(long offset) {
        return redisKey+":"+getKeyIndex(offset);
    }

    public long getKeyIndex(long offset){
        long index = 0;
        if(offset >= MB512){
            index = offset/MB512;
        }
        return index;
    }

    public void setKeySize() {
        if(getMaxbitSize() > MB512){
            this.keySize = getMaxbitSize()/MB512+1;
            System.out.println("超过MB512的限制，需要扩展，key个数为：" + keySize);
        }else {
            this.keySize = 0;
        }
    }
}