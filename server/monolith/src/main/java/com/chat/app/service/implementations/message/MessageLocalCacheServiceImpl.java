package com.chat.app.service.implementations.message;

import com.chat.app.payload.response.MessageResponse;
import com.chat.app.service.interfaces.message.caching.MessageLocalCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageLocalCacheServiceImpl implements MessageLocalCacheService {

    @Autowired
    private Cache<Long, List<MessageResponse>> localCache;


    @Override
    @Async
    public void setCache(Long chatId, List<MessageResponse> messages) {
        localCache.put(chatId, messages);
    }

    @Override
    public List<MessageResponse> getMessagesFromCache(Long chatId, int page, int size) {
        List<MessageResponse> cachedMessages = localCache.getIfPresent(chatId);
        if (cachedMessages == null) {
            return List.of();
        }
        return new PageImpl<>(cachedMessages, PageRequest.of(page, size), cachedMessages.size()).getContent();
    }

    @Override
    public void cacheNewMessage(Long chatId, MessageResponse response) {
        putMessagesToTheBottom(chatId, List.of(response));
    }

    @Override
    @Async
    public void updateMessageInCache(Long chatId, MessageResponse response) {
        List<MessageResponse> existingMessages = localCache.getIfPresent(chatId);
        if (existingMessages == null) {
            return;
        }
        existingMessages.stream()
                .filter(message -> message.getMessageId().equals(response.getMessageId()))
                .findFirst()
                .ifPresent(message -> {
                    int index = existingMessages.indexOf(message);
                    existingMessages.set(index, response);
                });
        setCache(chatId, existingMessages);
    }

    @Override
    @Async
    public void putMessagesToTheTop(Long chatId, List<MessageResponse> messageResponseList) {
        List<MessageResponse> existingMessages = localCache.getIfPresent(chatId);
        if (existingMessages != null) {
            messageResponseList.addAll(existingMessages);
        }
        localCache.put(chatId, messageResponseList);
    }

    @Override
    @Async
    public void putMessagesToTheBottom(Long chatId, List<MessageResponse> messageResponseList) {
        List<MessageResponse> existingMessages = localCache.getIfPresent(chatId);
        if (existingMessages == null) {
            localCache.put(chatId, messageResponseList);
            return;
        }
        existingMessages.addAll(messageResponseList);
        localCache.put(chatId, existingMessages);
    }

    @Override
    @Async
    public void removeMessageFromCache(Long chatId, Long messageId) {
        List<MessageResponse> existingMessages = localCache.getIfPresent(chatId);
        if (existingMessages == null) {
            return;
        }
        existingMessages.removeIf(message -> message.getMessageId().equals(messageId));
        localCache.put(chatId, existingMessages);
    }

    @Override
    public void clearCache(Long chatId) {
        localCache.invalidate(chatId);
    }
}
