package com.xck.bloomfilter;

import java.util.List;

public interface Bitmap {

    boolean set(List<Long> offsets);

    boolean set(long offset);

    boolean isExists(long[] offsets);

    boolean isExist(long offset);

    long getMaxbitSize();

    void setMaxbitSize(long maxbitSize);
}
