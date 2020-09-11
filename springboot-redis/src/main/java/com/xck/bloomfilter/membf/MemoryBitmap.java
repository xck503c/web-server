package com.xck.bloomfilter.membf;

import com.xck.bloomfilter.Bitmap;

import java.util.List;

public class MemoryBitmap implements Bitmap {

    //位图的key
    private String memKey;
    private long maxbitSize;
    private long[] bitMap;

    public MemoryBitmap(String memKey){
        this.memKey = memKey;
    }

    //批量设置位图中的位
    public boolean add(List<Long> offsets){
        for(int i=0; i<offsets.size(); i++){
            if(!isExist(offsets.get(i))){
                add(offsets.get(i));
            }

        }
        return true;
    }

    //批量获取位图中的位
    public List<Boolean> isExists(List<Long> offsets){
        for(int i=0; i<offsets.size(); i++){

        }
        return null;
    }

    public boolean add(long offset) {
        long bitBlock = bitMap[(int)(offset/64)];
        int bit = (int)(offset%64);
        bitMap[(int)(offset/64)] = (1<<bit) | bitBlock;
        return false;
    }

    public boolean isExist(long offset) {
        long bitBlock = bitMap[(int)(offset/64)];
        int bit = (int)(offset%64);;
        return ((bitBlock>>>bit) & 1) == 1;
    }

    public String getMemKey() {
        return memKey;
    }

    public void setMemKey(String memKey) {
        this.memKey = memKey;
    }

    public long getMaxbitSize() {
        return maxbitSize;
    }

    public void setMaxbitSize(long maxbitSize) {
        this.maxbitSize = maxbitSize;
        bitMap = new long[(int)(this.maxbitSize/64+1)];
    }

    public boolean remove(List<Long> offsets) {
        return false;
    }


}
