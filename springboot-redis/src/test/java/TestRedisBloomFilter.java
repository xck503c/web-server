import com.xck.bloomfilter.MemoryBitmap;
import com.xck.bloomfilter.RedisBitmap;
import com.xck.bloomfilter.RedisBloomFilter;

import java.util.ArrayList;
import java.util.List;

public class TestRedisBloomFilter {

    public static void main(String[] args) throws Exception{
        testRedisBitMaxBitSize();
    }

    public static void testRedisBitMaxBitSize(){
        RedisBitmap redisBitmap = new RedisBitmap("user_black_mobile");
        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(redisBitmap
                , 220000000, 0.0001f);
        //4217425698
        //4294967296
        System.out.println(512 * 8 * 1024 * 1024L);
//        redisBitmap.set(512 * 8 * 1024 * 1024L-1);
    }

    public static void testRedis() throws Exception{
        RedisBitmap redisBitmap = new RedisBitmap("user_black_mobile");

        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(redisBitmap
                , 15555555555L, 0.0001f);

        System.out.println("添加数据量: " + (55555555555L - 10000000000L)/1000);
        long start = System.currentTimeMillis();
        long count1 = 0;
        List<String> list = new ArrayList<String>(50);
        for(long i=1000; i<15555555555L; i++){
            list.add(i+"");
            count1++;
            if(list.size() == 60){
                redisBloomFilter.addBatch(list);
                list.clear();
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
}
