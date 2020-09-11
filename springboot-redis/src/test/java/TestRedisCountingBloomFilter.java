import com.xck.bloomfilter.cbf.RedisCountingBloomBitmap;
import com.xck.bloomfilter.cbf.RedisCountingBloomFilter;
import com.xck.redis.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class TestRedisCountingBloomFilter {

    public static void main(String[] args) throws Exception{

        test();
    }

    public static void test() throws Exception{
//        String redisKey = "";
//        RedisCountingBloomBitmap rcbb = new RedisCountingBloomBitmap(redisKey, 4);
//        RedisCountingBloomFilter rcbf = new RedisCountingBloomFilter(rcbb, 10, 0.01f);
//
//        //初始化账户级
//        System.out.println("初始化基础账户级------------------");
//        rcbf.add("zhangsan:1:216700000000");
//        System.out.println(rcbf.mightContain("zhangsan:1:216700000000"));;

        RedisPool redisPool = new RedisPool();
        redisPool.init();

        /**
         * getrange, redisKey: :0, bathindex: 81, byteindex: 40
         * getrange, redisKey: :0, bathindex: 61, byteindex: 30
         * getrange, redisKey: :0, bathindex: 9, byteindex: 4
         * getrange, redisKey: :0, bathindex: 53, byteindex: 26
         * getrange, redisKey: :0, bathindex: 33, byteindex: 16
         * getrange, redisKey: :0, bathindex: 77, byteindex: 38
         * getrange, redisKey: :0, bathindex: 57, byteindex: 28
         * bitcount, offset=81, firstBit=324, endbit=327
         * bitcount, offset=61, firstBit=244, endbit=247
         * bitcount, offset=9, firstBit=36, endbit=39
         * bitcount, offset=53, firstBit=212, endbit=215
         * bitcount, offset=33, firstBit=132, endbit=135
         * bitcount, offset=77, firstBit=308, endbit=311
         * bitcount, offset=57, firstBit=228, endbit=231
         */
//        Jedis jedis = redisPool.getJedis();
//        for(int i=0; i<400; i++){
//            if(jedis.getbit(":0", i)){
//                System.out.println(i);
//            }
//        }
//
        System.out.println("fffff");

    }

    public static void initData() throws Exception{
        String redisKey = "";
        RedisCountingBloomBitmap rcbb = new RedisCountingBloomBitmap(redisKey, 4);
        RedisCountingBloomFilter rcbf = new RedisCountingBloomFilter(rcbb, 50000000, 0.0005f);

        int count = 0;
        long logcount = 0;
        List<String> list = new ArrayList<String>(500);
        //初始化账户级
        System.out.println("初始化基础账户级------------------");
        for(long i=15700000000L; i<15700000000L+30000000L; i++){
            ++count;
            ++logcount;
            list.add("zhangsan:1:2" + String.valueOf(i));
            if(count % 60 == 0){
                rcbf.addBatch(list);
                list.clear();
                Thread.sleep(10);
            }
            if(logcount % 400000 == 0){
                System.out.println("数据量: " + logcount);
                logcount = 0;
                rcbf.printLogTime();
            }
        }

//        System.out.println("初始化服务器级别------------------");
//        for(long i=15700000000L; i<15700000000L+10000000L; i++){
//            ++count;
//            ++logcount;
//            list.add("zhangsan:3:1" + String.valueOf(i));
//            if(count % 60 == 0){
//                rcbf.addBatch(list);
//                list.clear();
//                Thread.sleep(10);
//            }
//            if(logcount % 400000 == 0){
//                System.out.println("数据量: " + logcount);
//                rcbf.printLogTime();
//            }
//        }
//
//        System.out.println("初始化客户营销级别------------------");
//        for(long i=15700000000L; i<15700000000L+10000000L; i++){
//            ++count;
//            ++logcount;
//            list.add("jwwww:5:52" + String.valueOf(i));
//            if(count % 60 == 0){
//                rcbf.addBatch(list);
//                list.clear();
//                Thread.sleep(10);
//            }
//            if(logcount % 400000 == 0){
//                System.out.println("数据量: " + logcount);
//                rcbf.printLogTime();
//            }
//        }
//
//        System.out.println("初始化客户级全局------------------");
//        for(long i=15700000000L; i<15700000000L+10000000L; i++){
//            ++count;
//            ++logcount;
//            list.add("jwwww:5:53" + String.valueOf(i));
//            if(count % 60 == 0){
//                rcbf.addBatch(list);
//                list.clear();
//                Thread.sleep(10);
//            }
//            if(logcount % 400000 == 0){
//                System.out.println("数据量: " + logcount);
//                rcbf.printLogTime();
//            }
//        }
    }

    public static void find() throws Exception{
        String redisKey = "";
        RedisCountingBloomBitmap rcbb = new RedisCountingBloomBitmap(redisKey, 4);
        RedisCountingBloomFilter rcbf = new RedisCountingBloomFilter(rcbb, 50000000, 0.0005f);

        long logcount = 0;
        long findcount = 0;
        //初始化账户级
        System.out.println("初始化基础账户级------------------");
        for(long i=15700000000L; i<15700000000L+50000000L; i++){
            ++logcount;
            if(rcbf.mightContain("zhangsan:1:2" + String.valueOf(i))){
                ++findcount;
            }
            if(logcount % 400000 == 0){
                System.out.println("数据量: " + logcount + ", 寻找: " + findcount);
                rcbf.printLogTime();
            }
        }

//        System.out.println("初始化服务器级别------------------");
//        for(long i=15700000000L; i<15700000000L+10000000L; i++){
//            ++logcount;
//            if(rcbf.mightContain("zhangsan:3:1" + String.valueOf(i))){
//                ++findcount;
//            }
//            if(logcount % 400000 == 0){
//                System.out.println("数据量: " + logcount + ", 寻找: " + findcount);
//                rcbf.printLogTime();
//            }
//        }
//
//        System.out.println("初始化客户营销级别------------------");
//        for(long i=15700000000L; i<15700000000L+10000000L; i++){
//            ++logcount;
//            if(rcbf.mightContain("jwwww:5:52" + String.valueOf(i))){
//                ++findcount;
//            }
//
//            if(logcount % 400000 == 0){
//                System.out.println("数据量: " + logcount + ", 寻找: " + findcount);
//                rcbf.printLogTime();
//            }
//        }
//
//        System.out.println("初始化客户级全局------------------");
//        for(long i=15700000000L; i<15700000000L+10000000L; i++){
//            ++logcount;
//            if(rcbf.mightContain("jwwww:5:53" + String.valueOf(i))){
//                ++findcount;
//            }
//            if(logcount % 400000 == 0){
//                System.out.println("数据量: " + logcount + ", 寻找: " + findcount);
//                rcbf.printLogTime();
//            }
//        }
    }
}
