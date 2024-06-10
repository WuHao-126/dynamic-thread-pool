package com.wuhao.sdk.registry.redis;


import com.wuhao.sdk.domain.model.valobj.RegistryEnumVO;
import com.wuhao.sdk.registry.IRegistry;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.springframework.beans.factory.DisposableBean;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    private final String appName;

    public RedisRegistry(RedissonClient redissonClient,String appName) {
        this.redissonClient = redissonClient;
        this.appName=appName;
    }

    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        String appName = threadPoolEntities.get(0).getAppName();
        List<ThreadPoolConfigEntity> collect = list.stream().filter(data -> data.getAppName().equals(appName)).collect(Collectors.toList());
        list.removeAll(collect);
        list.addAll(threadPoolEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getAppName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}
