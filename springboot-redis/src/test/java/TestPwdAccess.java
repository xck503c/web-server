import com.xck.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class TestPwdAccess {

    @Test
    public void test(){
        RedisPool redisPool = new RedisPool();
        redisPool.initPwd();

        Jedis jedis = redisPool.getJedis();
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        redisPool.returnJedis(jedis);
    }

    @Test
    public void testsentinelpwd(){
        RedisPool redisPool = new RedisPool();
        redisPool.initPwdSentinel();

        Jedis jedis = redisPool.getJedis();
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        redisPool.returnJedis(jedis);
    }
}
