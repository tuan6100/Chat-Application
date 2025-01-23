package com.chat.app.config;

import com.chat.app.utility.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.*;

import org.springframework.lang.NonNull;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    @Autowired
    @Lazy
    private TaskScheduler messageBrokerTaskScheduler;


    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/client").setHeartbeatValue(new long[]{10000, 10000}).setTaskScheduler(messageBrokerTaskScheduler);
        config.setApplicationDestinationPrefixes("/chat");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(), "/ws").setAllowedOrigins("ws://192.168.6.101:8000/ws", "ws://localhost:8000/ws");
    }
}