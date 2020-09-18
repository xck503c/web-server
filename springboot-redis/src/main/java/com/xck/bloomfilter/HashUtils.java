package com.xck.bloomfilter;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 哈希计算采用：
 * 1. https://github.com/olylakers/RedisBloomFilter/blob/master/src/main/java/org/olylakers/bloomfilter/HashUtils.java
 * 2. guava中的实现
 */
public class HashUtils {

    public static List<Long> bloomFilterHash(List<String> datas, int hashFuncCount, long maxbitSize) throws UnsupportedEncodingException{
        List<Long> offsets = new ArrayList<Long>(hashFuncCount);
        if(datas==null || datas.isEmpty()) return offsets;

        for(String data : datas){
            List<Long> offsetPart = bloomFilterHash(data, hashFuncCount, maxbitSize);
            if(offsetPart!=null && offsetPart.size()>0){
                offsets.addAll(offsetPart);
            }
        }
        return offsets;
    }

    public static List<Long> bloomFilterHash(String data, int hashFuncCount, long maxbitSize) throws UnsupportedEncodingException{
        return murmur3128InGuava(data, hashFuncCount, maxbitSize);
    }

    private static List<Long> murmur3128InGuava(String data, int hashFuncCount, long maxbitSize) {
        byte[] bytes = Hashing.murmur3_128().hashString(data, Charsets.UTF_8).asBytes();
        long hash1 = lowerEight(bytes);
        long hash2 = upperEight(bytes);

        long combinedHash = hash1;
        List<Long> offsets = new ArrayList<Long>(hashFuncCount);
        for(int i = 0; i<hashFuncCount; ++i) {
            offsets.add((combinedHash & Long.MAX_VALUE) % maxbitSize);
            combinedHash += hash2;
        }
        return offsets;
    }

    public static long lowerEight(byte[] bytes) {
        return Longs.fromBytes(bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
    }

    public static long upperEight(byte[] bytes) {
        return Longs.fromBytes(bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]);
    }

    /**
     * get the setbit offset by MurmurHash
     * @return
     */
//    private static long[] murmurHashOffset(String data, int hashFuncCount, long maxbitSize) throws UnsupportedEncodingException {
//        int hash1 = MurmurHash.hash(data.getBytes("UTF-8"), 0);
//        int hash2 = MurmurHash.hash(data.getBytes("UTF-8"), hash1);
//        long combinedHash = hash1;
//        long[] offsets = new long[hashFuncCount];
//        for(int i = 0; i<offsets.length; ++i) {
//            offsets[i] = (combinedHash & Long.MAX_VALUE) % maxbitSize;
//            combinedHash += hash2;
//        }
//        return offsets;
//    }
    public static void main(String[] args) throws Exception{
        System.out.println(bloomFilterHash("zhangsan:1:215700849893", 14, 41225624));
        System.out.println(bloomFilterHash("zhangsan:1:215700879745", 14, 41225624));
        System.out.println(bloomFilterHash("zhangsan:1:215700958342", 14, 41225624));
    }
}
