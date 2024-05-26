package com.wuhao.sdk.tigger.job;

import com.alibaba.fastjson.JSON;
import com.wuhao.sdk.domain.IDynamicThreadPoolService;
import com.wuhao.sdk.registry.IRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
@Slf4j
public class ThreadPoolDataReportJob {
    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }


    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList(){
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);
        log.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntities));
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            log.info("动态线程池，上报线程池配置：{}",JSON.toJSONString(threadPoolConfigEntity));
        }
    }
}
