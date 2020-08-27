package com.xck.bloomfilter;

import redis.clients.util.MurmurHash;

import java.io.UnsupportedEncodingException;

/**
 * 哈希计算采用：https://github.com/olylakers/RedisBloomFilter/blob/master/src/main/java/org/olylakers/bloomfilter/HashUtils.java
 */
public class HashUtils {

    /**
     * get the setbit offset by MurmurHash
     * @return
     */
    public static long[] murmurHashOffset(String data, int hashFuncCount, long maxbitSize) throws UnsupportedEncodingException {
        int hash1 = MurmurHash.hash(data.getBytes("UTF-8"), 0);
        int hash2 = MurmurHash.hash(data.getBytes("UTF-8"), hash1);

        long combinedHash = hash1;
        long[] offsets = new long[hashFuncCount];
        for(int i = 0; i<offsets.length; ++i) {
            offsets[i] = (combinedHash & Long.MAX_VALUE) % maxbitSize;
            combinedHash += hash2;
        }
        return offsets;
    }
}
