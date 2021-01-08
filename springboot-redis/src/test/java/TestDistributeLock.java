import com.xck.redisDistributeLock.RedisShareLock;

public class TestDistributeLock {

    public static void main(String[] args) {

    }

    private static class MyDisLockTask implements Runnable{

        @RedisShareLock
        @Override
        public void run() {

        }
    }

    private static class RedisSessionTask implements Runnable{

        @Override
        public void run() {
            Redi
        }
    }
}
