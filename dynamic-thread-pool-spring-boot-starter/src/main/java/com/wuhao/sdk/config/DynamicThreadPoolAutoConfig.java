package com.wuhao.sdk.config;

import com.wuhao.sdk.domain.DynamicThreadPoolService;
import com.wuhao.sdk.domain.IDynamicThreadPoolService;

import com.wuhao.sdk.domain.model.valobj.RegistryEnumVO;
import com.wuhao.sdk.exceptional.CustomRejectedExecutionHandler;
import com.wuhao.sdk.registry.IRegistry;
import com.wuhao.sdk.registry.redis.RedisRegistry;
import com.wuhao.sdk.tigger.job.ThreadPoolDataReportJob;
import com.wuhao.sdk.tigger.listener.ThreadPoolConfigAdjustListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private  String applicationName;

    @Autowired
    public DynamicThreadPoolAutoConfig(ApplicationContext applicationContext){
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            log.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }
    }


    @Bean
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap,RedissonClient redissonClient,CustomRejectedExecutionHandler customRejectedExecutionHandler){
        // 获取缓存数据，设置本地线程池配置
        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        for (String threadPoolKey : threadPoolKeys) {
            //向每个线程池配置异常，记录线程池出现异常的次数
            ThreadPoolExecutor threadPoolExecutor1 = threadPoolExecutorMap.get(threadPoolKey);
            threadPoolExecutor1.setRejectedExecutionHandler(customRejectedExecutionHandler);
            //读取配置，修改数据
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolKey).get();
            if (null == threadPoolConfigEntity) continue;
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolKey);
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }
        return new DynamicThreadPoolService(applicationName,threadPoolExecutorMap);
    }


    @Bean("redissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());
        RedissonClient redissonClient = Redisson.create(config);
        log.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient,applicationName);
    }



    @Bean
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, registry);
    }

    @Bean
    public ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolConfigAdjustListener(dynamicThreadPoolService, registry);
    }



    /**
     * 根据线程池获取对应Bean的名称
     * @param threadPoolExecutorMap
     * @return
     */
    @Bean("threadPoolExecutorNameMap")
    public Map<ThreadPoolExecutor,String> threadPoolExecutorNameMap(Map<String,ThreadPoolExecutor> threadPoolExecutorMap){
        Map<ThreadPoolExecutor,String> threadPoolExecutorNameMap=new HashMap<>();
        Set<String> threadPoolExecutorNames = threadPoolExecutorMap.keySet();
        for (String name : threadPoolExecutorNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(name);
            threadPoolExecutorNameMap.put(threadPoolExecutor,name);
        }
        return threadPoolExecutorNameMap;
    }

    /**
     * 设置自定义拒绝策略处理器
     * @return
     */
    @Bean
    public CustomRejectedExecutionHandler customRejectedExecutionHandler(Map<ThreadPoolExecutor,String> threadPoolExecutorNameMap){
        return new CustomRejectedExecutionHandler(applicationName,threadPoolExecutorNameMap);
    }
}
