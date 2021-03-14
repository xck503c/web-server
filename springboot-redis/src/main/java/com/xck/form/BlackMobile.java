package com.xck.form;

import com.xck.redis.RedisBytesParser;

public class BlackMobile implements RedisBytesParser {

    public String mobile;

    @Override
    public void parseBytes(byte[] bytes) {
        long lv = 0L;

        for(int i=0; i<bytes.length; i++){
            int offset = i << 3;
            lv = lv | ((((long)bytes[i]) & 0xff) << offset);
        }

        mobile = lv+"";
    }
}
