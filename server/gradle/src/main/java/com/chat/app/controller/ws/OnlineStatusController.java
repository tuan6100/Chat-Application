package com.chat.app.controller.ws;

import com.chat.app.model.redis.AccountOnlineStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class OnlineStatusController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/status")
    public void handleStatusUpdate(AccountOnlineStatus status) {
        messagingTemplate.convertAndSend("/client/online-status", status);
    }

}
