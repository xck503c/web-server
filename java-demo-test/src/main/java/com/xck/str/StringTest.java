package com.xck.str;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StringTest {

    public static void main(String[] args) throws Exception{
        System.out.println("UNDELIV".getBytes("ISO8859_1").length);
        System.out.println("DELIVRD".getBytes("ISO8859_1").length);
    }

    public static void HHmmss(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        System.out.println(format.format(new Date()));
    }

    public static void yyyyMMddHHmmss(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(new Date(System.currentTimeMillis() - 621*24*60*60*1000L)));
    }

    public static void percentStr(){
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        System.out.println(nf.format(0.3455));
    }

    public static void doubleFormatStr(){
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println(df.format(789.46846841));
    }
}
