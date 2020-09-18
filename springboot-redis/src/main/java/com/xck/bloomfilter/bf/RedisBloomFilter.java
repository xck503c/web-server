package com.xck.bloomfilter.bf;

import com.xck.bloomfilter.Bitmap;
import com.xck.bloomfilter.HashUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通版本的布隆，缺点，不能删除
 */
public class RedisBloomFilter {

    protected Bitmap bitmap;

    //需要映射的数据量
    protected long dataSize;

    //允许的错误率
    protected float errRate;

    //哈希函数的数量
    protected int hashFuncCount;

    public List<String> list = new ArrayList<String>(10000);

    protected long addBatchTime;
    protected long addBatchTimes;

    protected long findBatchTime;
    protected long findBatchTimes;

    public RedisBloomFilter(Bitmap _bitmap, long dataSize, float errRate){
        if(_bitmap == null) throw new IllegalArgumentException("RedisBloomFilter's redis map obj no init");
        if(errRate<0.0f || errRate>0.5f) throw new IllegalArgumentException("RedisBloomFilter errRate is (<0.0f or >0.5f");
        if(dataSize<0L) throw new IllegalArgumentException("RedisBloomFilter dataSize is <0");

        this.bitmap = _bitmap;
        this.dataSize = dataSize;
        this.errRate = errRate;
        bitmap.setMaxbitSize(calcBitSize());
        this.hashFuncCount = calcHashFuncCount(bitmap.getMaxbitSize());
        System.out.println("create bloom filter : dataSize=" + dataSize
                + ", errRate=" + errRate
                + ", bitmapSize=" + bitmap.getMaxbitSize()
                + ", hashFuncCount=" + hashFuncCount);
        //因为redis有限，所以通过计算key的数量来扩展
        if(bitmap instanceof RedisBloomBitmap){
            ((RedisBloomBitmap)bitmap).setKeySize();
        }
    }

    /**
     * 批量添加，首先先批量判断是否存在，不存在的就不添加了
     * @param datas
     * @return true-该批次添加成功
     * @throws UnsupportedEncodingException
     */
    public boolean addBatch(List<String> datas) throws UnsupportedEncodingException {
        List<Boolean> isContains = mightContains(datas);
        if(isContains.size() != datas.size()){
            System.out.println("datas : " + datas);
            System.out.println("iscontainssize : " + isContains.size());
        }

        long start = System.currentTimeMillis();
        List<Long> offsets;
        try {
            offsets = new ArrayList<Long>();
            for(int i=0; i<datas.size(); i++){
                if(isContains.get(i)){ //如果存在就不添加
                    System.out.println("存在: " + datas.get(i));
                    continue;
                }
                List<Long> tmp = HashUtils.bloomFilterHash(datas.get(i), hashFuncCount, bitmap.getMaxbitSize());
                if(tmp.size()>0){
                    offsets.addAll(tmp);
                }
            }

            return bitmap.add(offsets);
        } finally {
            addBatchTime+=(System.currentTimeMillis() - start);
            ++addBatchTimes;
        }
    }

    public boolean add(String data) throws UnsupportedEncodingException {
        if(mightContain(data)){ //如果存在就不添加
            return true;
        }
        return bitmap.add(HashUtils.bloomFilterHash(data, hashFuncCount, bitmap.getMaxbitSize()));
    }

    /**
     * 批量判断对应数据存在
     * @param datas
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<Boolean> mightContains(List<String> datas) throws UnsupportedEncodingException {
        long start = System.currentTimeMillis();
        List<Long> offsets = HashUtils.bloomFilterHash(datas, hashFuncCount, bitmap.getMaxbitSize());

        List<Boolean> isContains = new ArrayList<Boolean>(); //结果集合
        try {
            List<Boolean> results = bitmap.isExists(offsets);
            int count = 0;
            int falseCount = 0;
            for(Boolean r : results){
                ++count;
                if(!r){
                    ++falseCount;
                }

                if(count == hashFuncCount){
                    isContains.add(falseCount == 0 ? true : false);
                    count = 0;
                    falseCount = 0;
                }
            }
        } finally {
            findBatchTime+=(System.currentTimeMillis() - start);
            ++findBatchTimes;
        }
        return isContains;
    }

    /**
     * 对于key在布隆过滤器中是否存在
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean mightContain(String data) throws UnsupportedEncodingException {
        List<Boolean> results = bitmap.isExists(HashUtils.bloomFilterHash(data, hashFuncCount, bitmap.getMaxbitSize()));
        for(Boolean r : results){
            if(!r){
                return false;
            }
        }
        return true;
    }

    /**
     * 计算bit数组大小=-1*(dataSize)*ln(errRate)/(ln2)^2，最后向上取整
     *例如： 50000000,0.00001f --- 142MB
     * @return
     */
    private long calcBitSize(){
        return (long)Math.ceil(-1*dataSize*Math.log(errRate)/(Math.log(2)*Math.log(2)));
    }

    /**
     * 计算所需哈希函数的数量=bitSize/dataSize*ln2
     * @return
     */
    private int calcHashFuncCount(long bitSize){
        return (int) Math.ceil((bitSize / dataSize) * Math.log(2));
    }

    /**
     * 耗时打印，测试用
     */
    public void printLogTime(){
        if (addBatchTimes>0) {
            System.out.println("添加平均耗时: " + addBatchTime/addBatchTimes + "ms");
            addBatchTimes = 0;
            addBatchTime = 0;
        }

        if (findBatchTimes>0) {
            System.out.println("查询平均耗时: " + findBatchTime/findBatchTimes + "ms");
            findBatchTimes = 0;
            findBatchTime = 0;
        }
    }
}
