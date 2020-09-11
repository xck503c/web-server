package com.xck.bloomfilter.cbf;

import com.xck.bloomfilter.Bitmap;
import com.xck.bloomfilter.HashUtils;
import com.xck.bloomfilter.bf.RedisBloomFilter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * CBF的实现
 */
public class RedisCountingBloomFilter extends RedisBloomFilter {

    protected long removeBatchTime;
    protected long removeBatchTimes;

    public RedisCountingBloomFilter(Bitmap _bitmap, long dataSize, float errRate){
        super(_bitmap, dataSize, errRate);
    }

    /**
     * 批量移除，也是先判断后移除
     * @param datas
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean remove(List<String> datas) throws UnsupportedEncodingException{
        List<Boolean> isContains = mightContains(datas);

        long start = System.currentTimeMillis();
        List<Long> offsets;
        try {
            offsets = new ArrayList<Long>();
            for(int i=0; i<datas.size(); i++){
                if(!isContains.get(i)){ //如果不存在就不删除
                    continue;
                }
                List<Long> tmp = HashUtils.bloomFilterHash(datas.get(i), hashFuncCount, bitmap.getMaxbitSize());
                if(tmp.size()>0){
                    offsets.addAll(tmp);
                }
            }

            return bitmap.remove(offsets);
        } finally {
            removeBatchTime+=(System.currentTimeMillis() - start);
            ++removeBatchTimes;
        }
    }

    public boolean remove(String data) throws UnsupportedEncodingException {
        if(!mightContain(data)){ //如果不存在就不移除
            return true;
        }
        List<Long> offsets = new ArrayList<Long>();
        List<Long> tmp = HashUtils.bloomFilterHash(data, hashFuncCount, bitmap.getMaxbitSize());
        if(tmp.size()>0){
            offsets.addAll(tmp);
        }
        return bitmap.remove(offsets);
    }

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

        if (removeBatchTimes>0) {
            System.out.println("删除平均耗时: " + removeBatchTime/removeBatchTimes + "ms");
            removeBatchTimes = 0;
            removeBatchTime = 0;
        }
    }
}
