package com.wuhao.sdk.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClientConfig(){
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.useSingleServer()
                .setAddress("redis://localhost:6379") // Redis 服务器地址
                .setDatabase(0) // Redis 数据库编号
                .setPassword("12345678") // Redis 密码（如果有的话）
                .setConnectionPoolSize(10) // 连接池大小
                .setConnectionMinimumIdleSize(5); // 连接池最小空闲连接数
        return Redisson.create(config);
    }
}
