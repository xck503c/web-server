package com.xck.proxy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * json aspect
 *
 * @author xuchengkun
 * @date 2021/05/17 13:58
 **/
@Aspect
public class AspectJInJson {

    public static AspectJInJson aspectOf(){
        return new AspectJInJson();
    }

    @Around("execution(* com.alibaba.fastjson.JSON.toJSONString(java.lang.Object)) && args(obj)")
    public String parse2String(ProceedingJoinPoint join, Object obj){
        System.out.println("parse to String before");
        String str = "";
        try {
            str = (String) join.proceed(new Object[]{obj});
            System.out.println("result:"+str);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("parse to String after");
        return str;
    }
}
