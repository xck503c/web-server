package com.xck.jvm;

public class LocalVarTableTest {
    private  static byte[] bytes = null;

    public static void main(String[] args) throws Exception{
//        byte[] b = new byte[393219*1024];
//        b = null;
        Thread.sleep(15000);
        localvarGc();
    }

    public static void localvarGc(){
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

        bytes = new byte[(393210-2000)*1024];
        byte[] b = new byte[(2359296-393210-236005)*1024];
        b = null;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i=0; i<3000; i++){
            printGC();
            byte[] e1 = new byte[20*1024*1024];
            byte[] e2 = new byte[2*1024*1024];
            e1 = null;
            printGC();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int i = 0;
    }

    public static class LocalVarGC{
        public void gc1(){
            printGC();
            byte[] b = new byte[6*1024*1024];
            printGC();
            System.gc();
            printGC();
        }

        public void gc2(){
            printGC();
            byte[] b = new byte[6*1024*1024];
            printGC();
            b = null;
            printGC();
            System.gc();
            printGC();
        }

        public void gc3(){
            printGC();
            byte[] b = new byte[1600*1024*1024];
            b = null;
            printGC();
        }
    }

    public static void printGC(){
        double total = Runtime.getRuntime().totalMemory()/1024.0/1024.0;
        double free = Runtime.getRuntime().freeMemory()/1024.0/1024.0;

        System.out.printf("最大内存:%fMB, 空闲空间:%fMB, 可用空间:%fMB\n"
                , total, free, total-free);
    }
}
