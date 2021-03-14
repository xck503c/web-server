package com.xck.jdk.iterator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

public class IteratorEnumerationTest {

    public static void main(String[] args) {
        testHashTableIteratorAndEnum();
    }

    /**
     * 代码取自：https://www.cnblogs.com/skywang12345/p/3311275.html
     */
    public static void testHashTableIteratorAndEnum(){
        int val;
        Random r = new Random();
        Hashtable table = new Hashtable();
        for (int i=0; i<100000; i++) {
            // 随机获取一个[0,100)之间的数字
            val = r.nextInt(100);
            table.put(String.valueOf(i), val);
        }

        long iteratorTime = 0;
        long enumTime = 0;
        for (int i=0; i<100; i++) {
            long start = System.currentTimeMillis();
            // 通过Iterator遍历Hashtable
            iterateHashtable(table);
            iteratorTime += (System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            // 通过Enumeration遍历Hashtable
            enumHashtable(table);
            enumTime += (System.currentTimeMillis() - start);
        }

        System.out.println("iterator time : " + iteratorTime/100 + "ms");
        System.out.println("enum time : " + enumTime/100 + "ms");
    }

    /*
     * 通过Iterator遍历Hashtable
     */
    private static void iterateHashtable(Hashtable table) {
        Iterator iter = table.entrySet().iterator();
        while(iter.hasNext()) {
            //System.out.println("iter:"+iter.next());
            iter.next();
        }
    }

    /*
     * 通过Enumeration遍历Hashtable
     */
    private static void enumHashtable(Hashtable table) {
        Enumeration enu = table.elements();
        while(enu.hasMoreElements()) {
            enu.nextElement();
        }
    }
}
