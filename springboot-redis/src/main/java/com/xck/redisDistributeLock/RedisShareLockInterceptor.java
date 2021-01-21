package com.xck.redisDistributeLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * start around
 * before
 * 方法执行
 * end around
 * after
 */
@Aspect
@Component
public class RedisShareLockInterceptor {

    @Pointcut("@annotation(com.xck.redisDistributeLock.RedisShareLock)")
    public void redisShareLockPointCut(){
        System.out.println("redisShareLockPointCut");
    }

    @Before("redisShareLockPointCut()")
    public void before(){
        System.out.println("Before");
    }

    @Around("redisShareLockPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("start around"); //先执行
        Object result = joinPoint.proceed();
        System.out.println("process result=" + result);
        System.out.println("end around");
        return result;
    }

    @After("redisShareLockPointCut()")
    public void After(){
        System.out.println("After");
    }
}
