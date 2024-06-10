package com.wuhao.sdk.domain.model.entity;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
public class ThreadPoolExceptionNotice {
    private String appName;
    private String threadPoolName;
    private Integer exceptionCount;

    public ThreadPoolExceptionNotice() {
    }

    public ThreadPoolExceptionNotice(String appName, String threadPoolName, Integer exceptionCount) {
        this.appName = appName;
        this.threadPoolName = threadPoolName;
        this.exceptionCount = exceptionCount;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public Integer getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(Integer exceptionCount) {
        this.exceptionCount = exceptionCount;
    }
}
