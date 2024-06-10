package com.wuhao.sdk.config;


import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.wuhao.sdk.domain.model.entity.ThreadPoolExceptionNotice;
import com.wuhao.sdk.domain.model.valobj.RegistryEnumVO;
import com.wuhao.sdk.tigger.listener.ThreadPoolExceptionListener;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
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

    @Bean(name = "dynamicThreadPoolRedisExceptionTopic")
    public RTopic threadPoolExceptionListener(RedissonClient redissonClient, ThreadPoolExceptionListener threadPoolExceptionListener) {
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_EXCEPTION_TOPIC.getKey());
        topic.addListener(ThreadPoolExceptionNotice.class, threadPoolExceptionListener);
        return topic;
    }
}
