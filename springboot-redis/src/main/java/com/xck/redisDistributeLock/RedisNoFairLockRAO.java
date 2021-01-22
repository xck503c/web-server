package com.xck.redisDistributeLock;

import com.xck.redis.RedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisNoFairLockRAO {

    @Autowired
    RedisPool redisPool;


    /**
     * keys1 - 锁名字
     * args1 - 服务id，可以搞个uuid+mac，每次服务启动都会自己生成一个
     * args2 - 锁超时时间
     * 第一个判断如果没有进入，说明锁已经被人拿了
     * 第二个判断判断是不是自己的锁，不是自己的锁那就表示加锁失败；是自己的锁就刷新过期时间
     * 返回0，表示加锁成功
     * 返回非0，表示加锁失败，返回值表示锁的存活时间
     */
    private static String lockScript = "if (redis.call('setnx', KEYS[1], ARGV[1]) == 1) then " +
            "redis.call('pexpire', KEYS[1], ARGV[2]); " +
            "return 0; " +
            "end; " +
            "if (redis.call('get', KEYS[1]) == ARGV[1]) then " +
            "redis.call('pexpire', KEYS[1], ARGV[2]); " +
            "return 0; " +
            "end; " +
            "return redis.call('pttl', KEYS[1]);";

    /**
     * keys1 - 锁名字
     * args1 - 服务id，可以搞个uuid+mac，每次服务启动都会自己生成一个
     * 第一个判断如果没有进入，说明锁已经被人拿了
     * 第二个判断判断是不是自己的锁，不是自己的锁那就表示加锁失败；是自己的锁就刷新过期时间
     * 返回0，表示释放锁成功
     * 返回非0，表示释放锁失败，返回值表示锁的存活时间
     */
    private static String unlockScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then " +
            "redis.call('del', KEYS[1]); " +
            "return 0; " +
            "end; " +
            "return redis.call('pttl', KEYS[1]);";

    private static String continuationOfLifeScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then "
            + "redis.call('pexpire', KEYS[1], ARGV[2]); "
            + "return 0; "
            + "end; "
            + "return 1";

    public long lock(String lockKey, String taskId, long timeout){
        long result = -1;
        Jedis jedis = null;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                List<String> keys = new ArrayList<>(1);
                keys.add(lockKey);

                List<String> args = new ArrayList<>(2);
                args.add(taskId);
                args.add(timeout+"");

                result = (Long)jedis.eval(lockScript, keys, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisPool.returnJedis(jedis);
        }
        return result;
    }

    public long unlock(String lockKey, String taskId){
        Jedis jedis = null;
        long result = -1;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                List<String> keys = new ArrayList<>(1);
                keys.add(lockKey);

                List<String> args = new ArrayList<>(1);
                args.add(taskId);

                result = (Long)jedis.eval(unlockScript, keys, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisPool.returnJedis(jedis);
        }
        return result;
    }

    public long continuationOfLife(String lockKey, String taskId, long timeout){
        Jedis jedis = null;
        long result = -1;
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                List<String> keys = new ArrayList<>(1);
                keys.add(lockKey);

                List<String> args = new ArrayList<>(1);
                args.add(taskId);
                args.add(timeout+"");

                result = (Long)jedis.eval(continuationOfLifeScript, keys, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisPool.returnJedis(jedis);
        }
        return result;
    }
}
