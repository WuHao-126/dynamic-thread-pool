package com.wuhao.sdk.domain;



import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
public interface IDynamicThreadPoolService {
    /**
     * 获取线程池列表
     */
    List<ThreadPoolConfigEntity> queryThreadPoolList();

    ThreadPoolConfigEntity queryThreadPoolByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
