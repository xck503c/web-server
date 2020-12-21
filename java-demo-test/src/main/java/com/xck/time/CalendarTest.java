package com.xck.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarTest {

    private static SimpleDateFormat format
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        get();
    }

    public static void get(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDate(10, 12,25, 13, 11));
        System.out.println(format.format(calendar.getTime()));
        calendar.set(Calendar.DATE, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        System.out.println(format.format(calendar.getTime()));
    }

    public static Date getDate(int date, int hour, int min, int sec, int milli){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, milli);
        return calendar.getTime();
    }
}
