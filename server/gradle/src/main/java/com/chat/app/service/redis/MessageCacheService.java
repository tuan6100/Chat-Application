package com.chat.app.service.redis;


import com.chat.app.model.entity.Message;
import com.chat.app.model.redis.MessageCache;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.PrivateChatRepository;
import com.chat.app.repository.redis.MessageCacheRepository;
import com.chat.app.utility.CompositeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessageCacheService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageCacheRepository messageCacheRepository;

    @Autowired
    private PrivateChatRepository privateChatRepository;


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
        if (messageResponses.size() > size) {
            int end = messageResponses.size() - size * page;
            return messageResponses.subList(end - size, end);
        }
        return messageResponses;
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
            List<MessageResponse> messageResponses = new ArrayList<>(cache.getMessageResponses());
            messageResponses.add(response);
            cache.setMessageResponses(messageResponses);
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
                    if (cache == null || cache.getMessageResponses().stream().noneMatch(msg -> msg.getMessageId().equals(response.getMessageId()))) {
                        cacheNewMessage(chatId, accountId, response);
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
        System.out.println("Caching next messages for page: " + (page + 1));
        Pageable nextPage = PageRequest.of(page + 1, size);
        Page<Message> nextMessages = chatRepository.findLatestMessagesByChatId(chatId, nextPage);
        List<Message> nextMessagesList = nextMessages.getContent();
        List<MessageResponse> messageResponses = new ArrayList<>();
        nextMessagesList.forEach(message -> messageResponses.add(MessageResponse.fromEntity(message)));
        Collections.reverse(messageResponses);
        MessageCache cache = getCache(accountId, chatId);
        List<MessageResponse> existingMessages = new ArrayList<>(cache.getMessageResponses());
        messageResponses.addAll(existingMessages);
        cache.setMessageResponses(messageResponses);
        messageCacheRepository.save(cache);
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
            }
            cache.setMessageResponses(responses);
            messageCacheRepository.save(cache);
        }
    }
}
}
