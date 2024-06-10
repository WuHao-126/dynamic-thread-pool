package com.wuhao.sdk.exceptional;

import com.wuhao.sdk.domain.DynamicThreadPoolService;
import com.wuhao.sdk.domain.IDynamicThreadPoolService;
import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.wuhao.sdk.domain.model.entity.ThreadPoolExceptionNotice;
import com.wuhao.sdk.domain.model.valobj.RegistryEnumVO;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    private final String applicationName;

    private Map<ThreadPoolExecutor,String> threadPoolExecutorNameMap;

    private final ConcurrentHashMap<ThreadPoolExecutor, AtomicInteger> rejectionCounts = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<ThreadPoolExecutor, Long> lastNotificationTimes = new ConcurrentHashMap<>();

    @Autowired
    private Map<String,ThreadPoolExecutor> threadPoolExecutorMap;
    @Autowired
    private RedissonClient redissonClient;


    public CustomRejectedExecutionHandler(String applicationName,Map<ThreadPoolExecutor,String> threadPoolExecutorNameMap){
        this.applicationName=applicationName;
        this.threadPoolExecutorNameMap=threadPoolExecutorNameMap;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        //出现异常用计数器加减
        rejectionCounts.computeIfAbsent(executor, k -> new AtomicInteger(0)).incrementAndGet();
        String threadName = threadPoolExecutorNameMap.get(executor);
        System.out.println("线程池:"+threadName+"出现异常 异常出现次数: " + rejectionCounts.get(executor).get());
        //使用redis发布订阅发送通知
        long currentTime = System.currentTimeMillis();
        long lastNotificationTime = lastNotificationTimes.getOrDefault(executor, currentTime);
        long l = (currentTime - lastNotificationTime)/1000;
        if (l >= 10 || (currentTime-lastNotificationTime) ==0) {
            ThreadPoolExceptionNotice threadPoolExceptionNotice = new ThreadPoolExceptionNotice();
            threadPoolExceptionNotice.setAppName(applicationName);
            threadPoolExceptionNotice.setThreadPoolName(threadName);
            threadPoolExceptionNotice.setExceptionCount(rejectionCounts.get(executor).get());
            RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_EXCEPTION_TOPIC.getKey());
            topic.publish(threadPoolExceptionNotice);
            lastNotificationTimes.put(executor,currentTime);
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
        }
    }

    public int getRejectionCount(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        AtomicInteger atomicInteger = rejectionCounts.get(threadPoolExecutor);
        if(atomicInteger==null){
            return 0;
        }
        return atomicInteger.get();
    }
}
