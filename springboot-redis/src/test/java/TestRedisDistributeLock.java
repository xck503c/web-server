import com.xck.RunMain;
import com.xck.redis.RedisPool;
import com.xck.redisDistributeLock.RedisNoFairLockRAO;
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
    RedisPool redisPool;

    @Autowired
    RedisNoFairLockRAO redisNoFairLockRAO;

    private static int i = 0;
    private static CountDownLatch start = null;
    private static CountDownLatch end = null;

    @Test
    public void main() throws Exception{
        int threadSize = 3;

        start = new CountDownLatch(threadSize);
        end = new CountDownLatch(threadSize);

        for(int i=0; i<threadSize; i++){
            Thread t = new Thread(new IncTask(redisPool));
            t.start();
        }
//        Thread t = new Thread(new IncTaskMy(redisNoFairLockRAO, redisPool, false));
//        t.start();
//        t = new Thread(new IncTaskMy(redisNoFairLockRAO, redisPool, true));
//        t.start();
//        t = new Thread(new IncTaskMy(redisNoFairLockRAO, redisPool, false));
//        t.start();

        end.await();

        System.out.println(i);
    }

    private static class IncTask implements Runnable{

        private RedisPool redisPool;

        public IncTask(RedisPool redisPool) {
            this.redisPool = redisPool;
        }

        @Override
        public void run() {
            try {
                start.countDown();
                start.await();

                try {
                    for (int i=0; i<100; ++i) {
                        redisPool.inc();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                end.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    private static class IncTaskMy implements Runnable{
//
//        private RedisNoFairLockRAO redisNoFairLockRAO;
//        private RedisPool redisPool;
//        private boolean isBreak;
//
//        public IncTaskMy(RedisNoFairLockRAO redisNoFairLockRAO, RedisPool redisPool, boolean isBreak) {
//            this.redisNoFairLockRAO = redisNoFairLockRAO;
//            this.redisPool = redisPool;
//            this.isBreak = isBreak;
//        }
//
//        @Override
//        public void run() {
//            try {
//                start.countDown();
//                start.await();
//
//                long sTime = System.currentTimeMillis();
//                long startTime;
//                try {
//                    long lockResult = -1;
//                    for (int i=0; i<300; ++i) {
//                        while (true) {
//                            lockResult = redisNoFairLockRAO.lock("lockKey"
//                                    , Thread.currentThread().getId()+"", "10000");
//                            if(lockResult != 0){
//                                System.out.println(String.format("线程id: %d, 尝试拿锁失败, 时间戳: %d, 睡眠3s"
//                                        , Thread.currentThread().getId(), System.currentTimeMillis()));
//                                Thread.sleep(3000);
//                                continue;
//                            }
//                            break;
//                        }
//                        System.out.println(String.format("线程id: %d, 拿锁成功, 时间戳: %d"
//                                , Thread.currentThread().getId(), startTime = System.currentTimeMillis()));
//                        redisPool.inc();
//
//                        if(isBreak && System.currentTimeMillis() - sTime > 3000){
//                            System.out.println(i + "    break ---------------------------------------------------");
//                            break;
//                        }else {
//                            long unlockResult = redisNoFairLockRAO.unlock("lockKey", Thread.currentThread().getId()+"");
//                            if(unlockResult == 0){
//                                System.out.println(String.format("线程id: %d, 释放锁, 时间戳: %d, 耗时: %d"
//                                        , Thread.currentThread().getId(), System.currentTimeMillis()
//                                        , System.currentTimeMillis()-startTime));
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                end.countDown();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
