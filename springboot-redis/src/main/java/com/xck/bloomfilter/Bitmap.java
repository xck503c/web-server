package com.xck.bloomfilter;

public interface Bitmap {

    boolean set(long[] offsets);

    boolean set(long offset);

    boolean isExists(long[] offsets);

    boolean isExist(long offset);

    long getMaxbitSize();

    void setMaxbitSize(long maxbitSize);
}
