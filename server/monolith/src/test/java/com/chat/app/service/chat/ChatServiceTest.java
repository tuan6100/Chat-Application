package com.chat.app.service.chat;

import com.chat.app.model.entity.Message;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.service.implementations.chat.ChatServiceImpl;
import com.chat.app.service.interfaces.message.caching.MessageLocalCacheService;
import com.chat.app.service.interfaces.message.caching.MessageRedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageLocalCacheService messageLocalCacheService;

    @Mock
    private MessageRedisCacheService messageRedisCacheService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache chatMessagesCache;

    @InjectMocks
    private ChatServiceImpl chatServiceImpl;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMessagesByPage_CacheHit() {
        Long chatId = 2L;
        int page = 0;
        int size = 50;
        List<MessageResponse> cachedMessages = List.of(new MessageResponse());
        when(cacheManager.getCache("chatMessages")).thenReturn(chatMessagesCache);
        when(chatMessagesCache.get(chatId)).thenReturn(() -> cachedMessages);
        List<MessageResponse> result = chatServiceImpl.getMessagesByPage(chatId, page, size);
        assertEquals(cachedMessages, result);
        verify(messageRedisCacheService, never()).getMessagesFromCache(any(), anyInt(), anyInt());
        verify(chatRepository, never()).findMostRecentMessagesByChatId(any(), any(Pageable.class));
    }

    @Test
    public void testGetMessagesByPage_CacheMiss_RedisHit() {
        Long chatId = 2L;
        int page = 0;
        int size = 50;
        List<MessageResponse> redisMessages = List.of(new MessageResponse());
        when(cacheManager.getCache("chatMessages")).thenReturn(chatMessagesCache);
        when(chatMessagesCache.get(chatId)).thenReturn(null);
        when(messageRedisCacheService.getMessagesFromCache(chatId, page, size)).thenReturn(redisMessages);
        List<MessageResponse> result = chatServiceImpl.getMessagesByPage(chatId, page, size);
        assertEquals(redisMessages, result);
        verify(chatMessagesCache).put(chatId, redisMessages);
        verify(chatRepository, never()).findMostRecentMessagesByChatId(any(), any(Pageable.class));
    }

    @Test
    public void testGetMessagesByPage_CacheMiss_RedisMiss() {
        Long chatId = 2L;
        int page = 0;
        int size = 50;
        List<Message> messages = List.of(new Message());
        List<MessageResponse> messageResponses = List.of(new MessageResponse());
        Page<Message> messagePage = new PageImpl<>(messages);
        when(cacheManager.getCache("chatMessages")).thenReturn(chatMessagesCache);
        when(chatMessagesCache.get(chatId)).thenReturn(null);
        when(messageRedisCacheService.getMessagesFromCache(chatId, page, size)).thenReturn(Collections.emptyList());
        when(chatRepository.findMostRecentMessagesByChatId(chatId, PageRequest.of(page, size))).thenReturn(messagePage);
        when(messages.stream().parallel().map(MessageResponse::fromEntity).toList().reversed()).thenReturn(messageResponses);
        List<MessageResponse> result = chatServiceImpl.getMessagesByPage(chatId, page, size);
        assertEquals(messageResponses, result);
        verify(chatMessagesCache).put(chatId, messageResponses);
    }
}