package com.chat.app.service.interfaces.message.caching;


import com.chat.app.model.redis.MessageCache;
import com.chat.app.payload.response.MessageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageCacheService {

    void setCache(Long chatId, List<MessageResponse> messages);

    List<MessageResponse> getMessagesFromCache(Long chatId, int page, int size);

    void cacheNewMessage(Long chatId, MessageResponse response);

    void updateMessageInCache(Long chatId, MessageResponse response);

    void removeMessageFromCache(Long chatId, Long messageId);

    void clearCache(Long chatId);

}
