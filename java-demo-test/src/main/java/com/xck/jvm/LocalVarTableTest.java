package com.xck.jvm;

import java.util.regex.Pattern;

public class LocalVarTableTest {
    private  static byte[] bytes = null;

    public static void main(String[] args) throws Exception{
//        Thread.sleep(15000);
//        byte[] a1,a2,a3,a4;
//
//        a1 = new byte[2*1024*1024];
//        a2 = new byte[2*1024*1024];
//        a3 = new byte[2*1024*1024];
//        a4 = new byte[4*1024*1024];
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        String content = "2,3,4,5,6,7,8,9";

        String pattern = "(?=.*8)(?=.*9)^.*$";

        boolean isMatch = Pattern.matches(pattern, content);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
    }

//    public static void localvarGc(){
//        LocalVarGC localVarGC = new LocalVarGC();
        /**
         * 最大内存:123.000000MB, 空闲空间:119.743927MB, 可用空间:3.256073MB
         * 最大内存:123.000000MB, 空闲空间:113.087067MB, 可用空间:9.912933MB
         * 最大内存:123.000000MB, 空闲空间:116.182861MB, 可用空间:6.817139MB
         */
//        localVarGC.gc1();
        /**
         * 最大内存:123.000000MB, 空闲空间:119.743927MB, 可用空间:3.256073MB
         * 最大内存:123.000000MB, 空闲空间:113.087067MB, 可用空间:9.912933MB
         * 最大内存:123.000000MB, 空闲空间:113.087067MB, 可用空间:9.912933MB
         * 最大内存:123.000000MB, 空闲空间:122.182877MB, 可用空间:0.817123MB
         */
//        localVarGC.gc2();

//        testHandlePromotion();
//    }

//    public static void test1(){
//        bytes = new byte[(393210-2000)*1024];
//        byte[] b = new byte[(2359296-393210-236005)*1024];
//        b = null;
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        byte[] e3 = null;
//        for(int i=0; i<3000; i++){
//            printGC();
//            byte[] e1 = new byte[20*1024*1024];
//            byte[] e2 = new byte[2*1024*1024];
//            e1 = null;
//            printGC();
//            if(i == 50){
//                e3 = new byte[1024*1024*1024];
//            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        int i = 0;
//    }
//
    public final static int _1MB = 1024*1024;
//    public static void testHandlePromotion(){
//        byte[] a1,a2,a3,a4;
//
//        a1 = new byte[2*_1MB];
//        a2 = new byte[2*_1MB];
//        a3 = new byte[2*_1MB];
//        a4 = new byte[3*_1MB];
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static class LocalVarGC{
//        public void gc1(){
//            printGC();
//            byte[] b = new byte[6*1024*1024];
//            printGC();
//            System.gc();
//            printGC();
//        }
//
//        public void gc2(){
//            printGC();
//            byte[] b = new byte[6*1024*1024];
//            printGC();
//            b = null;
//            printGC();
//            System.gc();
//            printGC();
//        }
//
//        public void gc3(){
//            printGC();
//            byte[] b = new byte[1600*1024*1024];
//            b = null;
//            printGC();
//        }
//    }
//
//    public static void printGC(){
//        double total = Runtime.getRuntime().totalMemory()/1024.0/1024.0;
//        double free = Runtime.getRuntime().freeMemory()/1024.0/1024.0;
//
//        System.out.printf("最大内存:%fMB, 空闲空间:%fMB, 可用空间:%fMB\n"
//                , total, free, total-free);
//    }
}
