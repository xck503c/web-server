import com.xck.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestJavaOOM {

    //-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:
    @Test
    public void testOOM(){
        for(int i=0; i<1000000; i++){
            alloc();
        }
    }

    public void alloc(){
        byte[] a = new byte[1024*1024];
    }

    public static void main(String[] args) throws Exception{
//        Thread.sleep(15000);
        RedisPool testPool = new RedisPool();
        Jedis jedis = null;
        try {
            byte[] b = new byte[1024*1024*1024];
            for(int i=0; i<b.length; i++){
                b[i] = 1;
            }
            jedis = testPool.getJedis();
//        Thread.sleep(5000);
            jedis.set("bytes".getBytes(), b);
        } finally {
            testPool.returnJedis(jedis);
        }

    }
}
