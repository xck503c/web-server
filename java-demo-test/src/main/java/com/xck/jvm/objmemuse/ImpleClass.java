package com.xck.jvm.objmemuse;

/**
 * 实现接口类
 *
 * @author xuchengkun
 * @date 2021/04/08 11:04
 **/
public class ImpleClass implements Closeable{

    private static final long serialVersionUID = 7506256005612974639L;
    private int userSn;
    private long userSn1;
    private double userSn2;
    private String userId;
    private String userId1;
    private String userI2;

    @Override
    public void close() {
        System.out.println("close");
    }
}
