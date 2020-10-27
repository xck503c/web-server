package com.xck.longsmsdeal.db;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext apx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }
}
