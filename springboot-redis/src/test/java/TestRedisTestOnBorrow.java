import com.xck.redis.RedisPool;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class TestRedisTestOnBorrow {

    @Test
    public void testsmember(){
        String key = "xck123456";

        RedisPool pool = new RedisPool();
        pool.init();

        final AtomicInteger count = new AtomicInteger(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int last = 0;
                try {
                    while (true) {
                        Thread.sleep(1000);
                        int cur = count.get();
                        System.out.println("1s内自增: " + (cur-last));
                        last = cur;

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        long start = System.nanoTime();
        for(long i=5700000000L; i<5700000000L+200000; i++){
            pool.sismember(key, i+"");
            count.incrementAndGet();
        }
        System.out.println(System.nanoTime() - start);
    }
}
