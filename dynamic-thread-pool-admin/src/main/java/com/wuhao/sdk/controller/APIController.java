package com.wuhao.sdk.controller;

import com.alibaba.fastjson.JSON;
import com.wuhao.sdk.common.Result;


import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/thread")
public class APIController {

    @Autowired
    public RedissonClient redissonClient;

    @GetMapping("/list")
    public Result queryDynamicThreadPoolList(){
        RList<ThreadPoolConfigEntity> list = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
        try{
            return Result.success(list);
        }catch (Exception e){
            return Result.error(500,"线程池配置获取失败");
        }
    }

    @GetMapping("/name")
    public Result queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        try {
            String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            return Result.success(threadPoolConfigEntity);
        } catch (Exception e) {
            return Result.error(500,"线程池配置获取失败");
        }
    }

    @PostMapping("/update")
    public Result updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity request){
        try {
            log.info("修改线程池配置开始 {} {} {}", request.getAppName(), request.getThreadPoolName(), JSON.toJSONString(request));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + request.getAppName());
            topic.publish(request);
            log.info("修改线程池配置完成 {} {}", request.getAppName(), request.getThreadPoolName());
            return Result.success();
        } catch (Exception e) {
            return Result.error(500,"修改线程池配置异常");
        }
    }
}
