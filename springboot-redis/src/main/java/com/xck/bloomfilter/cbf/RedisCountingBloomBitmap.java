package com.xck.bloomfilter.cbf;

import com.xck.bloomfilter.bf.RedisBloomBitmap;
import com.xck.redis.RedisPipeline;
import com.xck.redis.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CBF的redis实现，专门和redis交互，可以提供的存在只有添加，删除和是否存在
 *
 * 基于3.0.7的版本上面实现，所以有些操作只能投机实现，比如说投机用get/setrange。。。
 */
public class RedisCountingBloomBitmap extends RedisBloomBitmap {

    //CBF的计数器所占的位数
    private int counterBitSize;
    private long maxBit;

    private RedisPool redisPool = new RedisPool();

    public RedisCountingBloomBitmap(String redisKey, int counterBitSize){
        super(redisKey);
        this.counterBitSize = counterBitSize;
    }

    public boolean add(List<Long> offsets){
        List<RedisStrByteOperInfo> operInfos = new ArrayList<RedisStrByteOperInfo>();
        for(Long l : offsets){
            RedisStrByteOperInfo info = new RedisStrByteOperInfo(getRedisKey(l), l, true);
            //0000 0000 0000 0000 0000 0000 0000
            //奇数就是低位，偶数就是高位
            info.setHigh(info.getBatchIndex()%2==0);
            info.setByteIndex(info.getBatchIndex()/2);
            operInfos.add(info);
        }

        index(operInfos);

        return true;
    }

    public boolean remove(List<Long> offsets){
        List<RedisStrByteOperInfo> operInfos = new ArrayList<RedisStrByteOperInfo>();
        for(Long l : offsets){
            operInfos.add(new RedisStrByteOperInfo(getRedisKey(l), l, false));
        }

        index(operInfos);

        return true;
    }

    //批量获取位图中的位
    public List<Boolean> isExists(List<Long> offsets){
        Jedis jedis = null;
        List<Boolean> isContains = new ArrayList<Boolean>();
        try {
            jedis = redisPool.getJedis();
            if (jedis != null) {
                Pipeline pipeline = jedis.pipelined();
                List<Response<byte[]>> result = new ArrayList<Response<byte[]>>(offsets.size());
                List<RedisStrByteOperInfo> infoList = new ArrayList<RedisStrByteOperInfo>();
                for(int i=0; i<offsets.size(); i++){
                    RedisStrByteOperInfo info = new RedisStrByteOperInfo();
                    infoList.add(info);
                    info.setByteIndex(offsets.get(i)/2);
                    info.setHigh(offsets.get(i)%2==0);
                    info.setBatchIndex(offsets.get(i));
                    result.add(pipeline.getrange(getRedisKey(offsets.get(i)).getBytes(), info.getByteIndex(), info.getByteIndex()));
                }
                pipeline.sync();
                if(result!=null && result.size()>0){

                    for(int i=0; i<result.size(); i++){
                        byte[] tmp = result.get(i).get();
                        if(tmp.length == 0){ //不存在
                            isContains.add(false);
                        }else if(tmp.length == 1){
//                            System.out.println(i + " " + tmp[0] + " " + infoList.get(i).isHigh + " " + infoList.get(i).getBatchIndex() + " " + infoList.get(i).getByteIndex());
                            if(infoList.get(i).isHigh && (((int)tmp[0]) & 0xf0) > 0){
                                isContains.add(true);
                            }else if((((int)tmp[0]) & 0x0f) > 0){
                                isContains.add(true);
                            }else {
                                isContains.add(false);
                            }
                        }else {
                            System.out.println("xxxxxx" + tmp.length);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            redisPool.returnJedis(jedis);
        }
        return isContains;
    }


    public boolean isExist(long offset) {
        return false;
    }

    public String getRedisKey(long offset){
        //0001 0010 0100 1000
        //3 - 1000
        //4*3=12
        long index = getKeyIndex(counterBitSize*offset);
        return redisKey + ":" + index;
    }

    public void setKeySize() {
        maxBit = getMaxbitSize()*counterBitSize;
        if(maxBit > MB512){
            this.keySize = maxBit/MB512+1;
            System.out.println("超过MB512的限制，需要扩展，key个数为：" + keySize);
        }else {
            this.keySize = 0;
        }
    }

    /**
     * 提供操作信息列表，根据提供的信息进行add或remove操作
     * @param list 操作信息列表
     * @return
     */
    private void index(List<RedisStrByteOperInfo> list){
        Jedis jedis = redisPool.getJedis();
        Pipeline pipeline = jedis.pipelined();
        List<Response<byte[]>> responses = new ArrayList<Response<byte[]>>();
        for(RedisStrByteOperInfo info : list){
            responses.add(pipeline.getrange(info.getRedisKey().getBytes(), info.getByteIndex(), info.getByteIndex()));
        }

        pipeline.sync();

        //因为这里可能出现获取同个字节索引的情况，所以要将相同的进行合并
        //byteindex --- bytevalue map
        Map<Long, Byte> byteIndexMapToByteValue = new HashMap<Long, Byte>();
        Map<Long, RedisStrByteOperInfo> byteIndexMapToOperInfo = new HashMap<Long, RedisStrByteOperInfo>();
        for(int i=0; i<responses.size(); i++){
            byte[] byteArr = responses.get(i).get();
            RedisStrByteOperInfo info = list.get(i);

            if(byteArr.length>1){
                System.out.println("取出字节数组大于1");
            }else{
                if (!byteIndexMapToByteValue.containsKey(info.getByteIndex())) {
                    byteIndexMapToByteValue.put(list.get(i).getByteIndex(), byteArr.length == 1 ? byteArr[0] : 0);
                    byteIndexMapToOperInfo.put(list.get(i).getByteIndex(), info);
                }
            }
        }
        redisPool.returnJedis(jedis);

        incOrDecInFourBitBatch(list, byteIndexMapToByteValue, byteIndexMapToOperInfo);
    }

    //批量操作
    private void incOrDecInFourBitBatch(List<RedisStrByteOperInfo> list, Map<Long, Byte> result, Map<Long, RedisStrByteOperInfo> byteIndexMapToOperInfo){
        for(int i=0; i<list.size(); i++){
            RedisStrByteOperInfo info = list.get(i);
            byte byteValue = result.get(info.getByteIndex());

            byte resultB = incOrDecInFourBit(byteValue, info.isHigh(), info.isInc());
            result.put(info.getByteIndex(), resultB);
        }

        Jedis jedis = redisPool.getJedis();
        Pipeline pipeline = jedis.pipelined();
        for(Long key : result.keySet()){
            pipeline.setrange(byteIndexMapToOperInfo.get(key).getRedisKey().getBytes(), key, new byte[]{result.get(key)});
        }

        pipeline.sync();
        redisPool.returnJedis(jedis);
    }

    /**
     * 根据所给信息，对字节b的高位低位进行增减操作，对溢出的情况进行打印操作
     * @param b
     * @param isHigh 是否是高位
     * @param isInc 是否是add
     * @return
     */
    private byte incOrDecInFourBit(byte b, boolean isHigh, boolean isInc){
        byte high = (byte) (b>>>4);
        byte low = (byte)((b<<4)>>>4);

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

        return c;
    }

    /**
     * 操作信息类，方便集中每个计数器操作的信息
     */
    private class RedisStrByteOperInfo{
        private String redisKey;
        private long batchIndex; //计数器的位置
        private boolean isInc; //是否add
        private boolean isHigh; //是否操作的是高位
        private long byteIndex; //定位计数器所在的字节位置

        public RedisStrByteOperInfo() {
        }

        public RedisStrByteOperInfo(String redisKey, long batchIndex, boolean isInc) {
            this.redisKey = redisKey;
            this.batchIndex = batchIndex;
            this.isInc = isInc;
        }

        public String getRedisKey() {
            return redisKey;
        }

        public void setRedisKey(String redisKey) {
            this.redisKey = redisKey;
        }

        public long getBatchIndex() {
            return batchIndex;
        }

        public void setBatchIndex(long batchIndex) {
            this.batchIndex = batchIndex;
        }

        public boolean isInc() {
            return isInc;
        }

        public void setInc(boolean inc) {
            isInc = inc;
        }

        public boolean isHigh() {
            return isHigh;
        }

        public void setHigh(boolean high) {
            isHigh = high;
        }

        public long getByteIndex() {
            return byteIndex;
        }

        public void setByteIndex(long byteIndex) {
            this.byteIndex = byteIndex;
        }
    }
}
