package com.xck.reflect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Category;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.helpers.AppenderAttachableImpl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Vector;

/**
 * 创建时间: 2020-11-18
 * 功能: 主要测试如果监控DailyRollingFileAppender的滚动方法
 */
public class DailyRollingFileAppenderRefect {

    public static Log log = LogFactory.getLog("info");

    public static void main(String[] args) throws Exception{
        log.info("ffsfdfsdfsdfdsfdsfsfdsffffffffffffffffffffffffffff");
        refect();
    }

    public static void refect() throws Exception{

        Log4JLogger log4JLogger = (Log4JLogger)log;
        Field loggerField = Log4JLogger.class.getDeclaredField("logger");
        loggerField.setAccessible(true);

        Category category = (Category)loggerField.get(log4JLogger);

        Field parentField = Category.class.getDeclaredField("parent");
        parentField.setAccessible(true);
        Category categorParent = (Category)parentField.get(category);

        Field aaiField = Category.class.getDeclaredField("aai");
        aaiField.setAccessible(true);

        AppenderAttachableImpl impl = (AppenderAttachableImpl)aaiField.get(categorParent);
        Field appenderField = AppenderAttachableImpl.class.getDeclaredField("appenderList");
        appenderField.setAccessible(true);

        Vector vector = (Vector)appenderField.get(impl);

        DailyRollingFileAppender appender = (DailyRollingFileAppender)vector.get(0);
        Field scheduledField = DailyRollingFileAppender.class.getDeclaredField("scheduledFilename");
        scheduledField.setAccessible(true);

        String scheduledFileName = (String)scheduledField.get(appender);

        Field nowField = DailyRollingFileAppender.class.getDeclaredField("now");
        nowField.setAccessible(true);
        Date nowDate = (Date)nowField.get(appender);

        Field nextCheck = DailyRollingFileAppender.class.getDeclaredField("nextCheck");
        nextCheck.setAccessible(true);
        long nextCheckL = (Long)nextCheck.get(appender);
        System.out.println(scheduledFileName + " " + nowDate + " " + nextCheckL);
    }
}
