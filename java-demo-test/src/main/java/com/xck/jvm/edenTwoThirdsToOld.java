package com.xck.jvm;

public class edenTwoThirdsToOld {

    public static void main(String[] args) throws Exception{
        Thread.sleep(15000);
        byte[] b = new byte[3*1024*1024];

        Thread.sleep(1000);
        byte[] b1 = new byte[3*1024*1024];
        Thread.sleep(3000);
    }
}
