import com.alibaba.fastjson.JSONObject;
import com.xck.form.TestClass;
import com.xck.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestRedisList {
    private final static String listKey = "test:list:num";
    private final static CountDownLatch latch = new CountDownLatch(8);
    private static RedisPool testPool = new RedisPool();
    private static AtomicInteger stopNum = new AtomicInteger();
    private static AtomicInteger popSum = new AtomicInteger();

    @Test
    public void testlpushMulti(){
        RedisPool pool = new RedisPool();

        List<String> list = new ArrayList<String>();
        for(int i=0; i<4000; i++){
            list.add(JSONObject.toJSONString(new TestClass("徐成昆", i)));
        }

        pool.lpushMultiPipe(listKey, list, 500);
    }

    @Test
    public void testPopMulit(){
        RedisPool pool = new RedisPool();
        List<String> list = pool.popMulitTransac(listKey, 1);
        System.out.println(list);
        System.out.println(list.size());
    }

    @Test
    public void popAll(){
        RedisPool pool = new RedisPool();
        Jedis jedis = pool.getJedis();
        List<Response<List<String>>> result = new ArrayList<Response<List<String>>>();
        try {
            Pipeline pipeline = jedis.pipelined();
            pipeline.multi();
            Response<List<String>> response = pipeline.lrange(listKey, -10, -1);
            pipeline.ltrim(listKey, 0, -11);
            result.add(response);
            pipeline.exec();
            pipeline.multi();
            Response<List<String>> response1 = pipeline.lrange(listKey, -10, -1);
            result.add(response1);
            pipeline.exec();
            pipeline.sync();
        } finally {
            pool.returnJedis(jedis);
        }
        for(Response<List<String>> responseTmp : result){
            List<String> tmp = responseTmp.get();
            System.out.println(tmp.size());
        }
    }

    @Test
    public void testPop(){
        RedisPool pool = new RedisPool();
        List<Object> list = pool.rpopMulit(listKey, 1000);
        System.out.println(list);
        System.out.println(list.size());
    }

    @Test
    public void llen(){
        RedisPool pool = new RedisPool();
        System.out.println(pool.listLen(listKey));
    }

    /**
     * push用时: 30369
     * push 1000 平均用时: 48
     * push用时: 30479
     * push 1000 平均用时: 48
     * push用时: 30492
     * push 1000 平均用时: 48
     * push用时: 30502
     * push 1000 平均用时: 49
     * pop总数量: 491212
     * pop用时: 29493
     * pop 1000 平均用时: 16
     * pop总数量: 508214
     * pop用时: 29493
     * pop 1000 平均用时: 17
     * pop总数量: 501221
     * pop用时: 29504
     * pop 1000 平均用时: 17
     * pop总数量: 500207
     * pop用时: 29516
     * pop 1000 平均用时: 17
     *
     * push 1000 平均用时: 56
     * push用时: 34739
     * push 1000 平均用时: 57
     * push用时: 34772
     * push 1000 平均用时: 56
     * push用时: 34780
     * push 1000 平均用时: 57
     * pop总数量: 1498000
     * pop用时: 33790
     * pop 1000 平均用时: 11
     * pop总数量: 1499000
     * pop用时: 33792
     * pop 1000 平均用时: 11
     * pop总数量: 1516000
     * pop用时: 33794
     * pop 1000 平均用时: 11
     * pop总数量: 1481000
     * pop用时: 33804
     * pop 1000 平均用时: 11
     * @throws Exception
     */
    @Test
    public void testlpushPop() throws Exception{
        int one = 10*10000;
        new Thread(new PushTask(0, one*5)).start();
        new Thread(new PushTask(one*5, one*10)).start();
        new Thread(new PushTask(one*10, one*15)).start();
        new Thread(new PushTask(one*15, one*20)).start();
//        new Thread(new PushTask(one*20, one*25)).start();
//        new Thread(new PushTask(one*25, one*30)).start();

        for(int i=0; i<4; i++){
            new Thread(new GetTask()).start();
        }
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    testPool.listLen(listKey);
                }
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    testPool.listLen(listKey);
                }
            }
        }).start();

        latch.await();

        while (stopNum.get()<8){
            Thread.sleep(1000);
        }
        System.out.println(popSum.get());
    }

    public class PushTask implements Runnable{
        private int start;
        private int end;

        public PushTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run() {
            latch.countDown();
            try {
                latch.await();
                System.out.println("PushTask start");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long startTime = System.currentTimeMillis();
            long time = 0L;
            List<String> list = new ArrayList<String>(1000);
            int pushTimes = 0;
            int count = 0;
            for(int i=start; i<end; i++){
                list.add(JSONObject.toJSONString(new TestClass("徐成昆", i)));
                if((i+1)%1000 == 0 && list.size()>0){
                    long startPush = System.currentTimeMillis();
                    testPool.lpushMulit(listKey, list);
                    count+=list.size();
                    pushTimes++;
                    time = time + (System.currentTimeMillis() - startPush);
                    list.clear();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(list.size()>0){
                long startPush = System.currentTimeMillis();
                testPool.lpushMulit(listKey, list);
                pushTimes++;
                count+=list.size();
                time = time + (System.currentTimeMillis() - startPush);
                list.clear();
            }
            System.out.println("push用时: " + (System.currentTimeMillis() - startTime));
            System.out.println("push 1000 平均用时: " + time/pushTimes);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopNum.incrementAndGet();
            System.out.println(count);
        }
    }

    public class GetTask implements Runnable{

        public void run() {
            latch.countDown();
            try {
                latch.await();
                System.out.println("GetTask start");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long startTime = System.currentTimeMillis();
            int pushTimes = 0;
            long time = 0L;
            int count = 0;
            while (true) {
                long startPush = System.currentTimeMillis();
                List<Object> list = testPool.rpopMulit(listKey, 1000);
                pushTimes++;
                Iterator<Object> it = list.iterator();
                while (it.hasNext()){
                    if (it.next() == null){
                        it.remove();
                    }
                }
                time = time + (System.currentTimeMillis() - startPush);
                count += list.size();

                if((list.isEmpty() || list.get(0)==null) && stopNum.get() >= 4){
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("pop总数量: " + count);
            System.out.println("pop用时: " + (System.currentTimeMillis() - startTime));
            System.out.println("pop 1000 平均用时: " + time/pushTimes);
            stopNum.incrementAndGet();
            popSum.getAndAdd(count);
        }
    }
}
