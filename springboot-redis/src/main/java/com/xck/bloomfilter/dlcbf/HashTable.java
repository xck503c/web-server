package com.xck.bloomfilter.dlcbf;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.xck.bloomfilter.HashUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 原实现地址：https://github.com/seiflotfy/dlCBF/blob/master/dlcbf.go
 */
public class HashTable {
    public final int bucketHeight = 8;

    private SubTable[] subTables;
    private int numTables;
    private int numBuckets;

    //1024*2048
    public HashTable(int numTables, int capacity){
        this.numTables = numTables;
        this.numBuckets = capacity/(numTables*bucketHeight);
    }

    public void add(String data) throws UnsupportedEncodingException {
        List<Target> list = getTarget(data, numTables, numBuckets);

    }

    public static List<Target> getTarget(String data, int tableCount, int bucketCount) {
        byte[] bytes = Hashing.murmur3_128().hashString(data, Charsets.UTF_8).asBytes();
        long hash1 = HashUtils.lowerEight(bytes);
        long hash2 = HashUtils.upperEight(bytes);

        long combinedHash = hash1;
        List<Target> offsets = new ArrayList<Target>(tableCount);
        for(int i = 0; i<tableCount; ++i) {
            Target target = new Target();
            target.bucketIndex = (int)((combinedHash & Long.MAX_VALUE) % bucketCount);
            target.fingerprint = (short)(data.hashCode());
            offsets.add(target);
            combinedHash += hash2;
        }
        return offsets;
    }

    public static class SubTable{
        Bucket[] buckets;
    }

    public static class Bucket{
        Cell[] fingerprints;
        byte counter;
    }

    public static class Cell{
        short fingerprints;
        byte counter;
    }

    public static class Target{
        int bucketIndex;
        short fingerprint;
    }
}
