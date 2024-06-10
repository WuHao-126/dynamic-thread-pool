package com.wuhao.sdk.tigger.listener;

import com.wuhao.sdk.domain.model.entity.ThreadPoolExceptionNotice;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: wuhao
 * @Datetime: TODO
 * @Description: TODO
 */
@Component
public class ThreadPoolExceptionListener implements MessageListener<ThreadPoolExceptionNotice> {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolExceptionNotice threadPoolExceptionNotice) {
        System.out.println("我接收到消息了："+threadPoolExceptionNotice.getAppName()+" "+threadPoolExceptionNotice.getExceptionCount());
        messagingTemplate.convertAndSend("/topic/warnings",threadPoolExceptionNotice);
    }
}
