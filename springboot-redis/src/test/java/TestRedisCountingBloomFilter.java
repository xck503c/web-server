import com.xck.bloomfilter.cbf.RedisCountingBloomBitmap;
import com.xck.bloomfilter.cbf.RedisCountingBloomFilter;
import com.xck.redis.RedisPool;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TestRedisCountingBloomFilter {

    public static void main(String[] args) throws Exception{
        test();
//        initData();
//        find();
    }

    public static void test() throws Exception{
        String redisKey = "test";
        RedisCountingBloomBitmap rcbb = new RedisCountingBloomBitmap(redisKey, 4);
        RedisCountingBloomFilter rcbf = new RedisCountingBloomFilter(rcbb, 10, 0.1f);

        /**
         * 存在: zhangsan:1:215700849893
         * 存在: zhangsan:1:215700879745
         * 存在: zhangsan:1:215700958342
         */
        //初始化账户级
//        System.out.println("初始化基础账户级------------------");
//        List<String> str = new ArrayList<String>();
//        str.add("zhangsan:1:215700849893");
//        rcbf.addBatch(str);
//        System.out.println(rcbf.mightContain("zhangsan:1:216700000000"));;

//        RedisPool redisPool = new RedisPool();
//        redisPool.init();

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

    /**
     * datas : [zhangsan:1:215721410900, zhangsan:1:215721410901, zhangsan:1:215721410902, zhangsan:1:215721410903, zhangsan:1:215721410904, zhangsan:1:215721410905, zhangsan:1:215721410906, zhangsan:1:215721410907, zhangsan:1:215721410908, zhangsan:1:215721410909, zhangsan:1:215721410910, zhangsan:1:215721410911, zhangsan:1:215721410912, zhangsan:1:215721410913, zhangsan:1:215721410914, zhangsan:1:215721410915, zhangsan:1:215721410916, zhangsan:1:215721410917, zhangsan:1:215721410918, zhangsan:1:215721410919, zhangsan:1:215721410920, zhangsan:1:215721410921, zhangsan:1:215721410922, zhangsan:1:215721410923, zhangsan:1:215721410924, zhangsan:1:215721410925, zhangsan:1:215721410926, zhangsan:1:215721410927, zhangsan:1:215721410928, zhangsan:1:215721410929, zhangsan:1:215721410930, zhangsan:1:215721410931, zhangsan:1:215721410932, zhangsan:1:215721410933, zhangsan:1:215721410934, zhangsan:1:215721410935, zhangsan:1:215721410936, zhangsan:1:215721410937, zhangsan:1:215721410938, zhangsan:1:215721410939, zhangsan:1:215721410940, zhangsan:1:215721410941, zhangsan:1:215721410942, zhangsan:1:215721410943, zhangsan:1:215721410944, zhangsan:1:215721410945, zhangsan:1:215721410946, zhangsan:1:215721410947, zhangsan:1:215721410948, zhangsan:1:215721410949]
     * iscontainssize : 49
     * Exception in thread "main" java.lang.IndexOutOfBoundsException: Index: 49, Size: 49
     * 	at java.util.ArrayList.rangeCheck(ArrayList.java:635)
     * 	at java.util.ArrayList.get(ArrayList.java:411)
     * 	at com.xck.bloomfilter.bf.RedisBloomFilter.addBatch(RedisBloomFilter.java:72)
     * 	at TestRedisCountingBloomFilter.initData(TestRedisCountingBloomFilter.java:72)
     * 	at TestRedisCountingBloomFilter.main(TestRedisCountingBloomFilter.java:13)
     * @throws Exception
     */
    public static void initData() throws Exception{
        String redisKey = "test";
        RedisCountingBloomBitmap rcbb = new RedisCountingBloomBitmap(redisKey, 4);
        RedisCountingBloomFilter rcbf = new RedisCountingBloomFilter(rcbb, 100000000, 0.00005f);

        int count = 0;
        long logcount = 0;
        int batchprint = 0;
        List<String> list = new ArrayList<String>(500);
        //初始化账户级
        System.out.println("初始化基础账户级------------------");
        for(long i=15700000000L+18800000; i<15700000000L+30000000; i++){
            ++count;
            ++logcount;
            list.add("zhangsan:1:2" + String.valueOf(i));
            if(count % 50 == 0){
                boolean isExc = true;
                while (isExc) {
                    try {
                        rcbf.addBatch(list);
                        isExc = false;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        isExc = true;
                        Thread.sleep(1000);
                    }
                }
                list.clear();
                Thread.sleep(5);
            }
            if(logcount % 400000 == 0){
                System.out.println("数据量: " + logcount + ", 次数:" + (++batchprint));
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
        String redisKey = "test";
        RedisCountingBloomBitmap rcbb = new RedisCountingBloomBitmap(redisKey, 4);
        RedisCountingBloomFilter rcbf = new RedisCountingBloomFilter(rcbb, 100000000, 0.00005f);

        long logcount = 0;
        long findcount = 0;
        int batchprint = 0;
        List<String> list = new ArrayList<String>(500);
        //初始化账户级
        System.out.println("查询基础账户级------------------");
        for(long i=15700000000L+200000000; i<15700000000L+230000000; i++){
            ++logcount;
            list.add("zhangsan:1:2" + String.valueOf(i));
            if(logcount % 1 == 0){
                List<Boolean> result = rcbf.mightContains(list);
                for(int j=0; j<result.size(); j++){
                    Boolean r = result.get(j);
                    if(r) ++findcount;
                }
                list.clear();
            }
//            if(rcbf.mightContain("zhangsan:1:2" + String.valueOf(i))){
//                ++findcount;
//            }
            if(logcount % 400000 == 0){
                System.out.println("数据量: " + logcount + ", 寻找: " + findcount + ", 次数: " + (++batchprint));
                rcbf.printLogTime();
            }
        }
        System.out.println("数据量: " + logcount + ", 寻找: " + findcount);

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
