package redisson;

import com.xck.RunMain;
import com.xck.redis.RedissonPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RunMain.class)
public class LockTest {

    @Autowired
    public RedissonPool redissonPool;

    public final static CountDownLatch start = new CountDownLatch(4);
    public final static CountDownLatch end = new CountDownLatch(4);

    @Test
    public void main() throws Exception{
        int size = 4;
        Thread[] threads = new Thread[size];
        for(int i=0; i<threads.length; i++){
            threads[i] = new Thread(new Use500msTask(redissonPool));
        }

        /**
         * start
         * 100936
         * 103055
         * 115781
         * 120010
         * total=120011
         */
        for(Thread thread : threads){
            thread.start();
        }
        start.await();
        System.out.println("start");
        long start = System.currentTimeMillis();

        end.await();
        System.out.println("total=" + (System.currentTimeMillis() - start));
    }

    public class Use500msTask implements Runnable{

        private RedissonPool redissonPool;

        public Use500msTask(RedissonPool redissonPool) {
            this.redissonPool = redissonPool;
        }

        @Override
        public void run() {
            start.countDown();
            try {
                start.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long start = System.currentTimeMillis();
            int i = 0;
            while (++i < 100) {
                RLock lock = redissonPool.getClient().getLock("lockKey");
                try {
                    lock.lock();
//                    System.out.println(111);
                    Thread.sleep(298);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
            System.out.println(System.currentTimeMillis()-start);
            end.countDown();
        }
    }
}
