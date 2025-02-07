package com.chat.app.service.interfaces.message.caching;

import com.chat.app.model.redis.MessageCache;
import org.springframework.stereotype.Service;

@Service
public interface MessageRedisCacheService extends MessageCacheService {

    MessageCache getCache(Long chatId);

    void cacheMessagesByPage(Long chatId, int page);
}
