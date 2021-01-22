package com.xck.redisDistributeLock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisNoFairLock {

    //redis锁名字
    String lockName();

    //redis锁过期时间
    long timeout() default 30000;

    //等待锁的时间
    long tryLockTimeInterval() default 3000;
}
