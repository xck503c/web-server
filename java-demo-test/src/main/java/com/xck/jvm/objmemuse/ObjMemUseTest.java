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
        System.out.println((true&&true||false)&&false );
        System.out.println(((true) && (true)) || ((false) && (false)));
    }

    public static void impleClass() {
        ImpleClass impleClass = new ImpleClass();
        System.out.println(ClassLayout.parseInstance(impleClass).toPrintable());
    }

    public static void stringClass() {
        String s = "1";
        System.out.println(ClassLayout.parseInstance(s).toPrintable());
    }

    public static void charArrClass() {
        char[] c = new char[]{'1', '1', '1'};
        System.out.println(ClassLayout.parseInstance(c).toPrintable());
    }

    public static void charClass() {
        Character c = '你';
        System.out.println(ClassLayout.parseInstance(c).toPrintable());
    }
}
