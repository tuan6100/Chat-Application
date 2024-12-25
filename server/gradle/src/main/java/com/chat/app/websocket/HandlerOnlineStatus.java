package com.chat.app.websocket;


import com.chat.app.service.AccountService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ServerEndpoint("/ws")
@Component
public class HandlerOnlineStatus {

    @Autowired
    private AccountService accountService;


    @OnOpen
    public void onOpen(Session session) {
        Long userId = getUserIdFromSession(session);
        accountService.markUserOnline(userId);
        System.out.println("User " + userId + " connected.");
    }

    @OnClose
    public void onClose(Session session) {
        Long userId = getUserIdFromSession(session);
        accountService.markUserOffline(userId);
        System.out.println("User " + userId + " disconnected.");
    }

    private Long getUserIdFromSession(Session session) {
        return (Long) session.getUserProperties().get("userId");
    }
}
