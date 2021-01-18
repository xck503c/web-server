package redisson;

import com.xck.RunMain;
import com.xck.redis.JdkObjValueStringCodec;
import com.xck.redis.RedisPool;
import com.xck.redis.RedissonPool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RunMain.class)
public class CommandTest {

    @Autowired
    public RedissonPool redissonPool;

    /**
     * jdk的序列化和redisson默认的反序列化不兼容
     * ==> java.io.IOException: java.lang.RuntimeException: unknown object tag -84
     *
     * @throws Exception
     */
    @Test
    public void putJdkObjAndGetBucket() throws Exception {
        String key = "user:xck01:name";

        RedisPool redisPool = new RedisPool();
        redisPool.init();
        Jedis jedis = redisPool.getJedis();
        redisPool.returnJedis(jedis);

        String s = "xck";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(s);
        byte[] t = baos.toByteArray();
        jedis.set(key.getBytes(), t);

        RBucket<Object> result = redissonPool.getClient().getBucket(key);
        System.out.println(result.get());
    }

    @Test
    public void scan() {
        Set<String> keys = new HashSet<>();
        RKeys rKeys = redissonPool.getClient().getKeys();
        Iterator<String> it = rKeys.getKeysByPattern("q1:*", 1).iterator();
        while (it.hasNext()) {
            String key = it.next();
            keys.add(key);
            System.out.println(key);
        }
        System.out.println(keys);
    }

    /**
     * 测试hscan，key-value为普通的String类型，非对象
     * ==> 需要灵活指定编码器
     */
    @Test
    public void hscanMapString() {
        String key = "user:xck01:info";

        RedisPool redisPool = new RedisPool();
        redisPool.init();
        Jedis jedis = redisPool.getJedis();
        Map<String, String> map = new HashMap<>();
        map.put("name", "xck");
        map.put("age", "1");
        jedis.hmset(key, map);
        redisPool.returnJedis(jedis);

        RMap rKeys = redissonPool.getClient().getMap(key, JdkObjValueStringCodec.INSTANCE);

//        Iterator<Map.Entry<String, String>> it = rKeys.entrySet("*", 1).iterator();
        Iterator<Map.Entry<String, String>> it = rKeys.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * 测试队列的出入队
     */
    @Test
    public void enOutQueue() throws Exception {
        String queueName = "msgQ1";
        int getSize = 1000;

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        boolean enResult = redissonPool.enQueue(queueName, list);
        Assert.assertEquals(enResult, true);
//        Assert.assertEquals(redissonPool.queueSize(queueName), 10);

        list.clear();

//        System.out.println(redissonPool.outQueue(queueName, getSize));
        System.out.println(redissonPool.outQueue(queueName, getSize));

//        Assert.assertEquals(redissonPool.queueSize(queueName), 0);

        Thread.sleep(10000);
    }

    /**
     * 测试队列的出入队
     */
    @Test
    public void enOutQueueMulti() throws Exception {
        System.out.println("111");
        final String queueName = "msgQ1";
        int getSize = 1000;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<Integer> list = new ArrayList<>();
                        for (int i = 0; i < 1; i++) {
                            list.add(i);
                        }
                        redissonPool.enQueue(queueName, list);
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS
                , queue);

        while (true) {
            if (queue.remainingCapacity() > 0) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        List<Object> result = redissonPool.outQueue(queueName, 1);
                        if (result.size() > 0) {
                            System.out.println("取出" + result.size());
                        }
                    }
                });
            } else {
                Thread.sleep(1000);
            }
        }
    }

    @Test
    public void lockTask() throws Exception {
        final String lockKey = "lockKey";

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                while (true) {
                    RLock rLock = redissonPool.getClient().getLock(lockKey);
                    try {
                        System.out.println(name + " 准备获取锁");
                        long start = System.currentTimeMillis();
                        rLock.lock();
                        System.out.println(name + " 拿到锁，耗时: " + (System.currentTimeMillis() - start));
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println(name + " 释放锁");
                        rLock.unlock();
                    }
                }
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        Thread t3 = new Thread(runnable);

        t1.start();
        t2.start();
        t3.start();

        Thread.sleep(30000000);
    }

    public class Sms implements Serializable {

        long timeStamp;
        String mobile = "";
        String content = "";

        public Sms(String mobile, String content) {
            this.timeStamp = System.currentTimeMillis();
            this.mobile = mobile;
            this.content = content;
        }

        @Override
        public String toString() {
            return "{" +
                    "timeStamp=" + timeStamp +
                    ", mobile='" + mobile + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
