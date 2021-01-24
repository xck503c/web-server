package com.xck.springannotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Classname Test
 * @Description TODO
 * @Date 2021/1/24 17:33
 * @Created by xck503c
 */
public class Test {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.refresh();
        applicationContext.scan("com.xck.springannotation");
        Man man = (Man)applicationContext.getBean("man");
        System.out.println(man.name);
    }
}
