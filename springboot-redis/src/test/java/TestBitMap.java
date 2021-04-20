import com.xck.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.*;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 位图测试
 *
 * @author xuchengkun
 * @date 2021/04/12 12:34
 **/
public class TestBitMap {

    public static final String[] REDIS_KEY_NET_SWITCH_MOBILE = {
            "", "net:switch:mobile:cm:", "net:switch:mobile:cu:","net:switch:mobile:ct:"
    };

    private static RedisPool pool = null;

    @Test
    public void testNetSwitchKey(){
        pool = new RedisPool();

        List<String> mobiles = new ArrayList<>();
        int count = 0;
        for(long i = 15720000000L; count<5000000; i=i+256L){

            mobiles.add(i+"");
            if(mobiles.size() >= 500){
                add(mobiles);
                mobiles.clear();
                System.out.println(count);
            }

            ++count;
        }
    }

    @Test
    public void t(){
        String curTime = new SimpleDateFormat("HH:mm").format(new Date());
        System.out.println(curTime);
    }

    private void add(List<String> mobiles){
        Jedis jedis = pool.getJedis();

        Pipeline pipeline = jedis.pipelined();
        for (String mobile : mobiles) {
            Long m = Long.parseLong(mobile);
            int index = getIndex(mobile);
            long offset = getoffset(mobile);
            if(m%3 == 0){ //移动
                setbitInPipe(pipeline, new boolean[]{true, false, false}, index, offset);
            }else if(m % 3 == 1){ //联通
                setbitInPipe(pipeline, new boolean[]{false, true, false}, index, offset);
            }else if(m % 3 == 2){ //电信
                setbitInPipe(pipeline, new boolean[]{false, false, true}, index, offset);
            }
        }
        pipeline.sync();

        pool.returnJedis(jedis);
    }

    private void setbitInPipe(Pipeline pipeline, boolean[] values, int index, long offset){
        if(pipeline == null) return;
        for(int i=1; i<REDIS_KEY_NET_SWITCH_MOBILE.length; i++){
            pipeline.setbit(REDIS_KEY_NET_SWITCH_MOBILE[i]+index, offset, values[i-1]);
        }
    }

    private int getIndex(String mobile){
        return Integer.parseInt(mobile.substring(1, 2));
    }

    private long getoffset(String mobile){
        return Long.parseLong(mobile.substring(2));
    }

    @Test
    public void testScanBitMap(){
        pool = new RedisPool();

        long size = 0;

        Set<String> set = scan("net:switch:mobile*", 1000);
        long start = System.currentTimeMillis();
        if(set!=null && !set.isEmpty()){
            for(String key : set){
                size+=scanNetSwitchKey(key);
            }
        }
        System.out.println(size);
        System.out.println(System.currentTimeMillis() - start);
    }

    public long scanNetSwitchKey(String netSwitchKey){
        long offsetByte = 0;

        int size = 0;

        while (offsetByte >= 0){
            long start = System.currentTimeMillis();
            List<Long> scanFirstBitInByteList = scanBitMap(netSwitchKey, offsetByte, 125000000, 200);
            if(scanFirstBitInByteList==null || scanFirstBitInByteList.isEmpty()) break;

            size += scanFirstBitInByteList.size();
//            System.out.println(getMobile(netSwitchKey, scanFirstBitInByteList));
            List<String> mobiles = getMobile(netSwitchKey, scanFirstBitInByteList);

            System.out.println("key=" + netSwitchKey + ", endMobile="+mobiles.get(mobiles.size()-1)
                    + ", useTime=" + (System.currentTimeMillis() - start) + ", size="+size);
            offsetByte = scanFirstBitInByteList.get(scanFirstBitInByteList.size()-1)/8+1;
        }

       return size;
    }

    public Set<String> scan(String pattern, int count) {
        Set<String> set = new HashSet<String>();
        Jedis jedis = pool.getJedis();
        if (jedis == null) {
            return set;
        }
        try {
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams();
            scanParams.count(count);
            scanParams.match(pattern);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> result = scanResult.getResult();
                if (result != null && result.size() > 0) {
                    set.addAll(result);
                }
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));
            return set;
        } catch (Exception e) {
            e.printStackTrace();
            return set;
        } finally {
            pool.returnJedis(jedis);
        }
    }

    private String[] zeroCache = {"", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000"
            , "000000000"};

    public List<String> getMobile(String key, List<Long> scanFirstBitInByteList){
        List<String> mobileList = new ArrayList<>();

        String s = key.substring(key.length()-1, key.length());

        Jedis jedis = pool.getJedis();
        Pipeline pipeline = jedis.pipelined();

        List<Response<byte[]>> responses = new ArrayList<>();
        for(Long offsetBit : scanFirstBitInByteList){
            responses.add(pipeline.getrange(key.getBytes(Charset.forName("utf-8")), offsetBit/8, offsetBit/8));
        }
        pipeline.sync();

        for(int i=0; i<responses.size(); i++){
            Response<byte[]> bytes = responses.get(i);
            long offsetBit = scanFirstBitInByteList.get(i);
            long first = (offsetBit/8)*8;
            byte mobiles = bytes.get()[0];
            for(int j=0; j<8; j++){
                int b = (mobiles >>> j) & 0x01;
                if(b == 1){
                    String byteoffset = (first + 7-j)+"";
                    byteoffset = zeroCache[(9-byteoffset.length())] + byteoffset;
                    mobileList.add("1" + s + byteoffset);
                }
            }
        }

        pool.returnJedis(jedis);

        return mobileList;
    }

    //125000000
    public List<Long> scanBitMap(String key, long start, long end, int num){
        List<Long> scanFirstBitInByteList = new ArrayList<>(num);

        Jedis jedis = pool.getJedis();
        do{
            start = start == 0 ? 0: start;
            long offsetBit = jedis.bitpos(key, true, new BitPosParams(start, end));
            if(offsetBit >= 0){
                scanFirstBitInByteList.add(offsetBit);
            } else{
                break;
            }
            start = offsetBit/8+1;
        }while (scanFirstBitInByteList.size() < num);

        pool.returnJedis(jedis);

        return scanFirstBitInByteList;
    }
}
