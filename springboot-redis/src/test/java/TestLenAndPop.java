import com.xck.redis.RedisPool;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestLenAndPop {
    private final static String listKey = "test:list:num";
    private final static CountDownLatch latch = new CountDownLatch(8);
    private static RedisPool testPool = new RedisPool();
    private static AtomicInteger stopNum = new AtomicInteger();

    @Test
    public void testLen() throws Exception{
        for(int i=0; i<8; i++){
            new Thread(new LlenTask()).start();
        }

        latch.await();

        while (stopNum.get()<8){
            Thread.sleep(1000);
        }
    }

    @Test
    public void testGet() throws Exception{
        for(int i=0; i<8; i++){
            new Thread(new GetTask()).start();
        }

        latch.await();

        while (stopNum.get()<8){
            Thread.sleep(1000);
        }
    }

    public class LlenTask implements Runnable{

        public void run() {
            latch.countDown();
            try {
                latch.await();
                System.out.println("LlenTask start");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long startTime = System.currentTimeMillis();
            int i = 0;
            long time = 0L;
            while (i++<200000) {
                long startPush = System.nanoTime();
                testPool.listLen(listKey);
                time = time + (System.nanoTime() - startPush);
            }
            System.out.println("llen用时: " + (System.currentTimeMillis() - startTime));
            System.out.println("llen 平均用时: " + time/i);
            stopNum.incrementAndGet();
        }
    }

    public class GetTask implements Runnable{

        public void run() {
            latch.countDown();
            try {
                latch.await();
                System.out.println("GetTask start");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long startTime = System.currentTimeMillis();
            int i = 0;
            long time = 0L;
            while (i++<200000) {
                long startPush = System.nanoTime();
                testPool.popMulitTransac(listKey, 1000);
                time = time + (System.nanoTime() - startPush);
            }
            System.out.println("pop用时: " + (System.currentTimeMillis() - startTime));
            System.out.println("pop 1000 平均用时: " + time/i);
            stopNum.incrementAndGet();
        }
    }
}
