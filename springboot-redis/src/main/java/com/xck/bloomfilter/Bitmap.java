package com.xck.bloomfilter;

import java.util.List;

public interface Bitmap {

    List<Boolean> isExists(List<Long> offsets);

    boolean isExist(long offset);

    long getMaxbitSize();

    void setMaxbitSize(long maxbitSize);

    boolean add(List<Long> offsets);

    boolean remove(List<Long> offsets);
}
