package com.xck.config;

import com.xck.redis.RedisProperties;
import com.xck.redis.RedissonPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname DBConfig
 * @Description TODO
 * @Date 2021/1/10 22:31
 * @Created by xck503c
 */
@Configuration
public class DBConfig {

    @Bean
    public RedissonPool redissonPool(RedisProperties redisProperties){
        return new RedissonPool().init(redisProperties);
    }
}
