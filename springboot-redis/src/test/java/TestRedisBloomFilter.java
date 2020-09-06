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
//        testcbf();
//        testStringCharisOneByte();
//        testOperInFourBitBatchAddOrDec();

        testRedisBitMaxBitSize();

    }

    /**
     * 主要测试java的hashcode算法，换成long类型
     * @param s
     * @return
     */
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

    /**
     * 主要测试一个redis字符串最大可以表示的位，可以用来储存多少规模的数据
     * 512MB最多是2.2亿
     */
    public static void testRedisBitMaxBitSize(){
        RedisBitmap redisBitmap = new RedisBitmap("user_black_mobile");
        RedisBloomFilter redisBloomFilter = new RedisBloomFilter(redisBitmap
                , 100000000, 0.000459f);
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

    /**
     * 主要测试和证明，getrange和setrange中一个偏移量是1个字节
     * @throws Exception
     */
    public static void testStringCharisOneByte() throws Exception{
        RedisPool redisPool = new RedisPool();
        redisPool.init();

        Jedis jedis = redisPool.getJedis();

        //证明：可以用ascii表示的，每个字符1个字节
        for(int i=0; i<2048; i++){
            jedis.append("a", ((char)1)+"");
        }
//
//
        jedis.setbit("a", 33, true);

        int count = 0;
        for(int i=0; i<128; i++){
            count++;
            System.out.print(jedis.getbit("a", i) + "  ");
            if(count % 8 == 0){
                System.out.println();
            }
        }
//        System.out.println((int)'1');
//        System.out.println((int)'9');

        //
//        for(int i=0; i<256; i++){
//            jedis.append("a", "9");
//        }

        //从0开始，第7个四位的位置，4*(7-1)=24个，换算成字节，从0开始第3个
        //0000 0000 0000 0000 0000 0000 0000 0000
//        long offset = 7;
//        long charindex = offset;
//        if(offset %2 != 0){
//            charindex = offset-1;
//        }
//        charindex = charindex/2;

        int charindex = 4;

        byte[] b = jedis.getrange("a".getBytes(), charindex, charindex);
        System.out.println(b.length); //1

        for(int i=0; i<b.length; i++){
            System.out.print(b[i] + "  ");
        }
        System.out.println();
//        jedis.setrange("a", charindex, ""+a);

//        System.out.println(jedis.getrange("a", charindex, charindex));
    }

    /**
     * 主要测试，可以通过任意4位一组的索引定位，从而实现+or-
     */
    public static void testOperInFourBitBatchAddOrDec(){
        index("a", 13, true);
    }

    /**
     *
     * @param batchIndex 从0开始
     * @return
     */
    private static void index(String key, long batchIndex, boolean isInc){
        //0000 0000 0000 0000 0000 0000 0000
        //奇数就是低位，偶数就是高位
        boolean isHigh = batchIndex%2==0;
        long byteIndx = batchIndex/2;

        RedisPool redisPool = new RedisPool();
        redisPool.init();

        Jedis jedis = redisPool.getJedis();

        byte[] b = jedis.getrange(key.getBytes(), byteIndx, byteIndx);
        if(b.length>1){
            System.out.println("取出字节数组大于1");
            return;
        }
        System.out.println("取出大小"+b[0]);

        int count = 0;
        for(int i=0; i<128; i++){
            count++;
            System.out.print(jedis.getbit("a", i) + "  ");
            if(count % 8 == 0){
                System.out.println();
            }
        }

        byte c = incOrDecInFourBit(b[0], isHigh, isInc);

        jedis.setrange(key.getBytes(), byteIndx, new byte[]{c});

        count = 0;
        for(int i=0; i<128; i++){
            count++;
            System.out.print(jedis.getbit("a", i) + "  ");
            if(count % 8 == 0){
                System.out.println();
            }
        }

        redisPool.returnJedis(jedis);
    }

    private static byte incOrDecInFourBit(byte b, boolean isHigh, boolean isInc){
        byte high = (byte) (b>>>4);
        byte low = (byte)((b<<4)>>>4);
        System.out.println(String.format("high=%d, low=%d", high, low));

        if(isHigh){
            if (isInc) {
                if(high == 15){
                    System.out.println("high++溢出");
                }else {
                    high++;
                }
            }else {
                if(high == 0){
                    System.out.println("high--溢出");
                }else{
                    high--;
                }
            }

        }else {
            if (isInc) {
                if(low == 15){
                    System.out.println("low++溢出");
                }else {
                    low++;
                }
            }else {
                if(low == 0){
                    System.out.println("low--溢出");
                }else {
                    low--;
                }
            }
        }

        byte c = (byte) (((high<<4)|0x0f)&(low|0xf0));

        System.out.println(String.format("high=%d, low=%d, c=%d", high, low, c));

        return c;

    }

}
