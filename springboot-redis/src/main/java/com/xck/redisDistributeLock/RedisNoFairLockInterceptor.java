package com.xck.redisDistributeLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * start around
 * before
 * 方法执行
 * end around
 * after
 *
 * redis非公平独占锁
 */
@Aspect
@Component
public class RedisNoFairLockInterceptor implements EnvironmentAware {

    @Autowired
    public RedisNoFairLockRAO redisNoFairLockRAO;

    private static String uuid = UUID.randomUUID().toString().replaceAll("-", "");

    private static Map<Long, String> threadTaskIdMap = new HashMap<>();
    private static Map<String, RedisLockWatchThread> timerThreadCenter = new ConcurrentHashMap<>();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 切点，标识在哪里切
     */
    @Pointcut(value = "@annotation(RedisNoFairLock)")
    public void redisNoFairLockPointCut(){}

    @Around("redisNoFairLockPointCut() && @annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisNoFairLock lock) throws Throwable {

        Long threadId = Thread.currentThread().getId();
        String taskId = threadTaskIdMap.get(threadId);
        if(taskId == null){
            threadTaskIdMap.put(threadId, taskId = (uuid + threadId));
        }

        //解析注解，找不到就返回原值
        String lockName = environment.resolvePlaceholders(lock.lockName());

        long lockResult = -1;
        Object result = null; //执行
        long start = 0;
        try {
            while (true) {
                lockResult = redisNoFairLockRAO.lock(lockName, taskId, lock.timeout());
                if(lockResult == 0){
                    break;
                }
                System.out.println(String.format("线程id: %d, 尝试拿锁失败, 时间戳: %d, 睡眠3s", threadId, System.currentTimeMillis()));
                if (lock.tryLockTimeInterval() < 1) {
                    Thread.sleep(lockResult);
                } else {
                    Thread.sleep(lock.tryLockTimeInterval());
                }
            }

            taskRunning(lockName, taskId, lock.timeout());

            result = joinPoint.proceed();
        } finally {
            if (lockResult == 0) {
                long unlockResult = redisNoFairLockRAO.unlock(lockName, taskId);
                if(unlockResult == 0){
                    System.out.println(String.format("线程id: %d, 释放锁, 时间戳: %d, 耗时: %d"
                            , threadId, System.currentTimeMillis(), System.currentTimeMillis()-start));
                    taskWait(lockName);
                }
            }
        }

        return result;
    }

    public void taskRunning(String redisKey, String taskId, long timeout){
        RedisLockWatchThread t = timerThreadCenter.get(redisKey);
        if(t == null){
            timerThreadCenter.put(redisKey, t = new RedisLockWatchThread(redisNoFairLockRAO, redisKey, timeout, taskId));
        }
        t.taskRunningState();
        if(t.getState() == Thread.State.NEW){
            t.start();
        }
    }

    public void taskWait(String redisKey){
        RedisLockWatchThread t = timerThreadCenter.get(redisKey);
        if(t != null){
            t.lockWaitState();
        }
    }
}
