package com.xck.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Classname RedisProperties
 * @Description TODO
 * @Date 2021/1/10 22:22
 * @Created by xck503c
 */
@Setter
@Getter
@ToString
@Configuration
@PropertySource(value = "classpath:db.properties", encoding = "utf-8")
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String url;
    private String sentinelUrl;
    private int connectPoolSize = 20;
    private int minIdleSize = 10;
    private int connectTimeout = 15000;
    private int timeout = 5000;
    private String masterName = "myMaster";
    private String clientName = "";
    private String pwd;
    private String mode = "signle";
}
