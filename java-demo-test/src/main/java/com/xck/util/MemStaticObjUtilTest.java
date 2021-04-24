package com.xck.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试类
 *
 * @author xuchengkun
 * @date 2021/04/20 11:35
 **/
public class MemStaticObjUtilTest {

    public static Map<String, Object> map = new HashMap<>();

    public static void main(String[] args) throws Exception{
        map.put("a", "f");
        map.put("b", 1);
        System.out.println(map.get("a"));

        Thread.sleep(30000000);
    }
}
