package com.chat.app.service.implementations.message;


import com.chat.app.model.entity.Message;
import com.chat.app.model.redis.MessageCache;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.redis.MessageCacheRepository;
import com.chat.app.service.interfaces.message.caching.MessageRedisCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageRedisCacheServiceImpl implements MessageRedisCacheService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageCacheRepository messageCacheRepository;

    @Value("${spring.cache.distributed.message-per-chat}")
    private int messagePerChat;

    public static final List<Integer> cachedPages = List.of(0, 1);


    @Override
    public void setCache(Long chatId, List<MessageResponse> messages, int page) {
        MessageCache cache = getCache(chatId);
        if (cache == null) {
            cache = new MessageCache(chatId, messages);
        } else {
            cache.setMessageResponses(messages);
        }
        messageCacheRepository.save(cache);
    }

    @Override
    public MessageCache getCache(Long chatId) {
        return messageCacheRepository.findById(chatId.toString()).orElse(null);
    }

    @Override
    public List<MessageResponse> getMessagesFromCache(Long chatId, int page, int size) {
        List<MessageResponse> messageResponses = getCache(chatId).getMessageResponses();
        if (messageResponses == null || !cachedPages.contains(page)) {
            return List.of();
        }
        return new PageImpl<>(messageResponses, PageRequest.of(page, size), messageResponses.size()).getContent();
    }

    @Override
    @Async
    public void cacheNewMessage(Long chatId, MessageResponse response) {
        MessageCache cache = getCache(chatId);
        if (cache == null) {
            cache = new MessageCache(chatId, new ArrayList<>(Collections.singletonList(response)));
        } else {
            List<MessageResponse> messageResponses = cache.getMessageResponses();
            messageResponses.add(response);
            if (messageResponses.size() > messagePerChat) {
                messageResponses.removeFirst();
            }
        }
        messageCacheRepository.save(cache);
    }

    @Override
    @Async
    public void updateMessageInCache(Long chatId, MessageResponse response) {
        MessageCache cache = getCache(chatId);
        if (cache != null) {
            List<MessageResponse> existingMessages = cache.getMessageResponses();
            existingMessages.stream()
                    .filter(message -> message.getMessageId().equals(response.getMessageId()))
                    .findFirst()
                    .ifPresent(message -> {
                        int index = existingMessages.indexOf(message);
                        existingMessages.set(index, response);
                    });
            cache.setMessageResponses(existingMessages);
            messageCacheRepository.save(cache);
        }
    }

    @Override
    @Async
    public void cacheMessagesByPage(Long chatId, int page) {
        Pageable pageable = PageRequest.of(page, messagePerChat);
        Page<Message> messages = chatRepository.findMostRecentMessagesByChatId(chatId, pageable);
        setCache(chatId, messages.stream().parallel().map(MessageResponse::fromEntity).toList().reversed(), page);
    }

    @PostConstruct
    public void cacheMostRecentMessagesForAllChats() {
        List<Long> chatIds = chatRepository.findAllChatIds();
        chatIds.stream().parallel().forEach(chatId -> {
            if (getCache(chatId) == null || getCache(chatId).getMessageResponses().isEmpty()) {
                cacheMessagesByPage(chatId, 0);
            } else {
                System.out.println("Cache for chat " + chatId + " already exists");
            }
        });
    }

    @Override
    @Async
    public void removeMessageFromCache(Long chatId, Long messageId) {
        MessageCache cache = getCache(chatId);
        if (cache != null) {
            cache.getMessageResponses().removeIf(response -> response.getMessageId().equals(messageId));
            messageCacheRepository.save(cache);
        }
    }

    @Override
    @Async
    public void clearCache(Long chatId) {
        messageCacheRepository.deleteById(chatId.toString());
    }
}