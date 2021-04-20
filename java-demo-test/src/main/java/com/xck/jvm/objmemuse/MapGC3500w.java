package com.xck.jvm.objmemuse;

import org.openjdk.jol.info.ClassLayout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 利用map进行状态匹配，主要测试Map占用数据量大的问题。
 *
 * 测试数据是700w，key长度12，value长度43，占用空间1.6g，对应t1
 * t2是第一次优化，用于value里面有重复数据
 * t3是用set集合来代替hashmap
 *
 * @author xuchengkun
 * @date 2021/04/06 11:16
 **/
public class MapGC3500w {

    public static void main(String[] args) throws Exception{
        t2();
    }

    public static void t1() throws Exception{
        HashMap<String, String> testMap = new HashMap<>();

        int i=0, sn=1000000;
        for(long mobile=15720000000L; mobile<15720000000L+7000000; mobile++){
            String key = mobile + "" + i;
            String value = sn + "submit_message_send_history_20210406";

            testMap.put(key, value);

            System.out.println(i);

            i++;
        }

        for(int j=0; j<1000; j++){
            Thread.sleep(3000);
        }
    }

    public static void t2() throws Exception{
        HashMap<String, String> testMap = new HashMap<>();

        Map<Integer, String> tableMap = new HashMap<>();
        tableMap.put(10, "submit_message_send_history_20210406");

        int i=0, sn=1000000;
        for(long mobile=15720000000L; mobile<15720000000L+7000000; mobile++){
            String key = mobile + "" + i;
            String value = sn + "10";

            testMap.put(key, value);

            System.out.println(i);

            i++;
        }

        for(int j=0; j<1000; j++){
            Thread.sleep(3000);
        }
    }

    public static void t3() throws Exception{
        Map<Integer, String> tableMap = new HashMap<>();
        tableMap.put(10, "submit_message_send_history_20210406");

        Set<Test> set = get();

        System.gc();

        System.out.println(ClassLayout.parseInstance(set).toPrintable());


        for(int j=0; j<1000; j++){
            Thread.sleep(3000);
        }
    }

    public static Set<Test> get(){
        Set<Test> testMap = new HashSet<>(3600000, 0.85f);

        long i=0, sn=100000000;
        for(long mobile=15720000000L; mobile<15720000000L+7000000; mobile++){
            String key = mobile + "" + i;
            long value = sn*100 + 10;

            Test t = new Test(key, value);
            testMap.add(t);
            System.out.println(ClassLayout.parseInstance(t).toPrintable());

            System.out.println(i);

            i++;
        }

        return testMap;
    }

    private static class Test{
        char[] key;
        long value;

        public Test(String key, long value) {
            this.key = key.toCharArray();
            this.value = value;
        }

        @Override
        public int hashCode() {
            if (key.length > 0) {
                int h = 0;
                char val[] = key;

                for (int i = 0; i < key.length; i++) {
                    h = 31 * h + val[i];
                }
                return h;
            }
            return 0;
        }
    }
}