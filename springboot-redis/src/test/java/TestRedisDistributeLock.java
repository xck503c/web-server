import com.xck.RunMain;
import com.xck.redisDistributeLock.RedisNoFairLockRAO;
import com.xck.redisDistributeLock.RedisShareLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RunMain.class)
public class TestRedisDistributeLock {

    @Autowired
    RedisNoFairLockRAO redisNoFairLockRAO;

    private static int i = 0;
    private static CountDownLatch start = null;
    private static CountDownLatch end = null;

    @Test
    public void main() throws Exception{
        int threadSize = 6;

        start = new CountDownLatch(threadSize);
        end = new CountDownLatch(threadSize);

        for(int i=0; i<threadSize; i++){
            Thread t = new Thread(new IncTask(redisNoFairLockRAO));
            t.start();
        }

        end.await();

        System.out.println(i);
    }

    @Test
    public void ana() throws Exception{
        redisNoFairLockRAO.aaaa();
    }

    private static class IncTask implements Runnable{

        private String id;
        private RedisNoFairLockRAO redisNoFairLockRAO;

        public IncTask(RedisNoFairLockRAO redisNoFairLockRAO) {
            this.redisNoFairLockRAO = redisNoFairLockRAO;
        }

        @Override
        public void run() {
            try {
                id = Thread.currentThread().getName();
                start.countDown();
                start.await();

                int count = 0;
                while (true){
                    long result = redisNoFairLockRAO.lock("countKey", id, "30000");
                    if(result == 0){
                        System.out.println(id + " 加锁成功");
                        try{
                            ++i;
                            System.out.println(i);
                            ++count;
                            Thread.sleep(100);
                        }finally {
                            redisNoFairLockRAO.unlock("countKey", id);
                            System.out.println(id + " 解锁成功 count=" + count);
                            Thread.sleep(100);
                        }
                    }else {
                        if(result == -1){
                            Thread.sleep(3000);
                            continue;
                        }else {
                            Thread.sleep(1000);
                        }
                    }
                    if(count >= 100){
                        break;
                    }
                }

                end.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
