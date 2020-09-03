import com.xck.bloomfilter.MemoryBitmap;
import com.xck.bloomfilter.RedisBitmap;
import com.xck.bloomfilter.RedisBloomFilter;
import com.xck.bloomfilter.RedisCountingBitmap;
import com.xck.redis.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class TestRedisBloomFilter {

    public static void main(String[] args) throws Exception{
//        testMem();
//        System.out.println(hashCode("zzzzzz9999999999999999"));
//        System.out.println(0|1);
//        testRedis();
        testcbf();
    }

    public static long hashCode(String s) {
        char[] value = s.toCharArray();
        long h = 0;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
        }
        return h;
    }

    public static void testRedisBitMaxBitSize(){
        RedisBitmap redisBitmap = new RedisBitmap("user_black_mobile");
        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(redisBitmap
                , 100000000, 0.00001f);
        //2396264600
        //4294967296
        System.out.println(512 * 8 * 1024 * 1024L);
//        redisBitmap.set(512 * 8 * 1024 * 1024L-1);
    }

    public static void testRedis() throws Exception{
        RedisBitmap redisBitmap = new RedisBitmap("user_black_mobile");

        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(redisBitmap
                , 60000000, 0.0001f);

        System.out.println("添加数据量: " + (55555555555L - 10000000000L)/1000);
        long start = System.currentTimeMillis();
        long count1 = 0;
        List<String> list = new ArrayList<String>(50);
        for(long i=0; i<15000000; i++){
            list.add(i+"");
            count1++;
            if(list.size() == 60){
                long start1 = System.currentTimeMillis();
                redisBloomFilter.addBatch(list);
                list.clear();
                System.out.println(System.currentTimeMillis() - start1);
            }
            if(count1 %10000 == 0){
                System.out.println(count1 + "  " + redisBloomFilter.list.size());
                Thread.sleep(100);
            }
        }
        System.out.println(redisBloomFilter.list);
        System.out.println("数据添加完成，耗时: " + (System.currentTimeMillis()-start)+"ms");

//        System.out.println("开始测试数据:");
//        long count = 0;
//        long sum = 0L;
//        //    9997000
//        //10000000000
//        //0 1000 2000 3000 4000 5000 6000 7000 8000 9000
//        for(long i=10000000000L; i<55555555555L; i+=10){
//            long start = System.currentTimeMillis();
//            boolean result = redisBloomFilter.mightContain(i+"");
//            sum += (System.currentTimeMillis()-start);
//            if(result){
//                count++;
//                if(count % 10000 == 0){
//                    System.out.println(count + " " + (i-10000000000L) + " " + sum);
//                    sum = 0L;
//                }
//            }
//        }
//        System.out.println("命中数量: " + count);
    }

    public static void testMem() throws Exception{
        MemoryBitmap redisBitmap = new MemoryBitmap("user_black_mobile");
        redisBitmap.setMaxbitSize(200000000);

        long start = System.currentTimeMillis();
        long count1 = 0, count2=0;
        for(long i=0; i<12; i++){
            count1++;
            long index = (i+"").hashCode()%20000000;
            if(1568==index){
                int a = 0;
            }
            if (!redisBitmap.isExist(index)) {
                redisBitmap.set(index);
            }else {
                System.out.println(i);
                count2++;
            }
            if(count1 %1000000 == 0){
                System.out.println(count1 + "  " + count2);
            }
        }
        System.out.println("数据添加完成，耗时: " + (System.currentTimeMillis()-start)+"ms");
    }

    public static void testcbf(){
        RedisCountingBitmap redisBitmap = new RedisCountingBitmap("user_black_mobile", 4);
        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(redisBitmap
                , 100000000, 0.0001f);
    }
}
