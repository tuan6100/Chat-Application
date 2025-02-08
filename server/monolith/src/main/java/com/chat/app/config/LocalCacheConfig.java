package com.chat.app.config;


import com.chat.app.payload.response.MessageResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching(proxyTargetClass = true)
public class LocalCacheConfig {

    @Value("${spring.cache.in-memory.message-per-chat}")
    private int messagePerChat;


    @Bean
    public Cache<Long, List<MessageResponse>> chatMessagesCache() {
        return Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(messagePerChat)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

}
