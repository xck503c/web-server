package com.xck.bloomfilter;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class RedisBloomFilter {

    private Bitmap bitmap;

    //需要映射的数据量
    private long dataSize;

    //允许的错误率
    private float errRate;

    //哈希函数的数量
    private int hashFuncCount;

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
    }

    public boolean addBatch(List<String> datas) throws UnsupportedEncodingException {
        long[] offsets = new long[datas.size() * hashFuncCount];
        int cursor = 0;
        for(String data : datas){
            long[] tmp = HashUtils.murmurHashOffset(data, hashFuncCount, bitmap.getMaxbitSize());
            for(int i=0; i<tmp.length; i++){
                offsets[cursor++] = tmp[i];
            }
        }

        return bitmap.set(offsets);
    }

    public boolean add(String data) throws UnsupportedEncodingException {
        return bitmap.set(HashUtils.murmurHashOffset(data, hashFuncCount, bitmap.getMaxbitSize()));
    }

    public boolean mightContain(String data) throws UnsupportedEncodingException {
        return bitmap.isExists(HashUtils.murmurHashOffset(data, hashFuncCount, bitmap.getMaxbitSize()));
    }

    /**
     * 计算bit数组大小=-1*(dataSize)*ln(errRate)/(ln2)^2，最后向上取整
     *例如： 50000000,0.00001f --- 142MB
     * @return
     */
    private int calcBitSize(){
        return (int)Math.ceil(-1*dataSize*Math.log(errRate)/(Math.log(2)*Math.log(2)));
    }

    /**
     * 计算所需哈希函数的数量=bitSize/dataSize*ln2
     * @return
     */
    private int calcHashFuncCount(long bitSize){
        return (int) Math.ceil((bitSize / dataSize) * Math.log(2));
    }

}
