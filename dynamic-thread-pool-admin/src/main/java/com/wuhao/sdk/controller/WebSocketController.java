package com.wuhao.sdk.controller;

import com.wuhao.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin("*")
public class WebSocketController {

    @MessageMapping("/checkValue")
    @SendTo("/topic/warnings")
    public Object checkValue(int value) {
        System.out.println(6666);
        ThreadPoolConfigEntity entity = new ThreadPoolConfigEntity();
        entity.setRemainingCapacity(20);
        entity.setActiveCount(30);
        entity.setCorePoolSize(30);
        int threshold = 100; // 阈值
        if (value > threshold) {
            return "数值已超过阈值!";
        }
        return "数值正常";
    }
}
