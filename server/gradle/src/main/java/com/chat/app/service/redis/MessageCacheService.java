package com.chat.app.service.redis;


import com.chat.app.model.entity.Message;
import com.chat.app.model.redis.MessageCache;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.PrivateChatRepository;
import com.chat.app.repository.redis.MessageCacheRepository;
import com.chat.app.utility.CacheSyncManager;
import com.chat.app.dto.CompositeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MessageCacheService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageCacheRepository messageCacheRepository;

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private CacheSyncManager cacheSyncManager;


    public MessageCache getCache(Long accountId, Long chatId) {
        CompositeKey key = new CompositeKey(accountId, chatId);
        return messageCacheRepository.findById(key.toString()).orElse(null);
    }

    public boolean existsInCache(Long accountId, Long chatId) {
        MessageCache cache = getCache(accountId, chatId);
        return cache != null && !cache.getMessageResponses().isEmpty();
    }

    public List<MessageResponse> getMessagesFromCache(Long accountId, Long chatId, int page, int size) {
        List<MessageResponse> messageResponses = getCache(accountId, chatId).getMessageResponses();
        int totalMessages = messageResponses.size();
        int end = totalMessages - size * page;
        int start = Math.max(0, end - size);
        if (end <= 0) {
            return Collections.emptyList();
        }
        return messageResponses.subList(start, end);
    }

    public void setCache(Long chatId, Long accountId, List<MessageResponse> messages) {
        MessageCache cache = getCache(accountId, chatId);
        if (cache == null) {
            cache = new MessageCache(new CompositeKey(accountId, chatId), messages);
        } else {
            cache.setMessageResponses(messages);
        }
        messageCacheRepository.save(cache);
    }

    public void cacheNewMessage(Long chatId, Long accountId, MessageResponse response) {
        MessageCache cache = getCache(accountId, chatId);
        if (cache == null) {
            cache = new MessageCache(new CompositeKey(accountId, chatId), new ArrayList<>(Collections.singletonList(response)));
        } else {
            cache.getMessageResponses().add(response);
            System.out.println("Cached message: " + response.getContent() + " into table: " + accountId + ":" + chatId);
        }
        messageCacheRepository.save(cache);
    }

    @Async
    public void cacheNewMessage(Long chatId, List<Long> accountIds, MessageResponse response) {
        ExecutorService executor = Executors.newFixedThreadPool(accountIds.size());
        List<CompletableFuture<Void>> futures = accountIds.stream()
                .map(accountId -> CompletableFuture.runAsync(() -> {
                    MessageCache cache = getCache(accountId, chatId);
                    if (cache == null) {
                        cacheNewMessage(chatId, accountId, response);
                    } else {
                        List<MessageResponse> responses = cache.getMessageResponses();
                        int index = -1;
                        for (int i = responses.size() - 1; i >= 0; i--) {
                            if (responses.get(i).getMessageId().equals(response.getMessageId())) {
                                index = i;
                                break;
                            }
                        }
                        if (index >= 0) {
                            responses.set(index, response);
                        } else {
                            responses.add(response);
                        }
                        cache.setMessageResponses(responses);
                        messageCacheRepository.save(cache);
                    }
                }, executor))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }


    @Async
    public void removeMessageFromCache(Long chatId, Long accountId, Long messageId) {
        MessageCache cache = getCache(accountId, chatId);
        if (cache != null) {
            cache.getMessageResponses().removeIf(response -> response.getMessageId().equals(messageId));
            messageCacheRepository.save(cache);
        }
    }

    @Async
    public void cacheNextMessages(Long chatId, Long accountId, int page, int size) {
        CompositeKey key = new CompositeKey(accountId, chatId);
        AtomicBoolean isUpdating = cacheSyncManager.getOrCreateUpdateFlag(key);
        CountDownLatch latch = cacheSyncManager.getOrCreateLatch(key);
        if (isUpdating.get()) {
            return;
        }
        isUpdating.set(true);
        try {
            Pageable nextPage = PageRequest.of(page, size);
            Page<Message> nextMessages = chatRepository.findLatestMessagesByChatId(chatId, nextPage);
            if (!nextMessages.isEmpty()) {
                List<MessageResponse> responses = new ArrayList<>();
                for (Message message : nextMessages) {
                    responses.add(MessageResponse.fromEntity(message));
                }
                Collections.reverse(responses);
                MessageCache cache = getCache(accountId, chatId);
                if (cache != null && cache.getMessageResponses() != null) {
                    responses.addAll(cache.getMessageResponses());
                }
                setCache(chatId, accountId, responses);
            }
        } finally {
            isUpdating.set(false);
            latch.countDown();
        }
    }

    @Async
    public void restoreDefaultCache(Long accountId) {
        List<Long> chatIds = privateChatRepository.findPrivateChatsByAccountId(accountId);
        for (Long chatId : chatIds) {
            MessageCache cache = getCache(accountId, chatId);
            if (cache != null) {
                List<MessageResponse> responses = cache.getMessageResponses();
                if (responses.size() > 50) {
                    responses = responses.subList(responses.size() - 50, responses.size());
                } else if (responses.size() < 50 ) {
                    responses.clear();
                    cacheNextMessages(chatId, accountId, 0, 50);
                }
                cache.setMessageResponses(responses);
                messageCacheRepository.save(cache);
            }
        }
    }
}