package com.xck.config;

import com.xck.redis.RedissonPool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname DBConfig
 * @Description TODO
 * @Date 2021/1/10 22:31
 * @Created by xck503c
 */
@Configuration
@EnableConfigurationProperties
public class DBConfig {

    @Bean(destroyMethod = "close")
    public RedissonPool redissonPool(RedisProperties redisProperties){
        return new RedissonPool().init(redisProperties);
    }
}
