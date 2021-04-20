package com.xck.jvm.objmemuse;

import org.openjdk.jol.info.ClassLayout;

/**
 * 对象内存占用测试
 *
 * @author xuchengkun
 * @date 2021/04/08 11:03
 **/
public class ObjMemUseTest {

    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            System.out.println("daemon " + Thread.currentThread().getName());
                        }
                    }
                });
                t.setDaemon(true);
                t.setName("xxxxfefdsf");
                t.start();
                while (true){
                    System.out.println(Thread.currentThread().getName());
                }
            }
        });
        t.setName("xxxx");
        t.start();
    }

    public static void impleClass() {
        ImpleClass impleClass = new ImpleClass();
        System.out.println(ClassLayout.parseInstance(impleClass).toPrintable());
    }

    public static void stringClass() {
        String s = "11111";
        System.out.println(ClassLayout.parseInstance(s).toPrintable());
    }

    public static void charArrClass() {
        char[] c = new char[]{'1', '1', '1'};
        System.out.println(ClassLayout.parseInstance(c).toPrintable());
    }
}
