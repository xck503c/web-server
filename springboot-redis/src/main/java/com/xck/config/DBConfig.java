package com.xck.config;

import com.xck.redis.RedisPool;
import com.xck.redis.RedissonPool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Classname DBConfig
 * @Description TODO
 * @Date 2021/1/10 22:31
 * @Created by xck503c
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties
public class DBConfig {

    @Bean(destroyMethod = "close")
    public RedissonPool redissonPool(RedisProperties redisProperties){
        return new RedissonPool().init(redisProperties);
    }

    @Bean(destroyMethod = "close")
    public RedisPool redisPool(){
        RedisPool testPool = new RedisPool();
        testPool.init();
        return testPool;
    }
}
