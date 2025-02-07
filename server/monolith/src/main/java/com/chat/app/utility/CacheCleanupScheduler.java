package com.chat.app.utility;

import com.chat.app.model.redis.MessageCache;
import com.chat.app.service.interfaces.message.caching.MessageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheCleanupScheduler {

    @Autowired
    private RedisTemplate<String, MessageCache> redisTemplate;

    @Autowired
    private MessageCacheService messageCacheService;


//    @Scheduled(fixedRate = 60 * 60 * 1000)
//    public void cleanupExpiredMessageCaches() {
//        ScanOptions scanOptions = ScanOptions.scanOptions().match("MessageCache:*").count(100).build();
//        Cursor<String> cursor = redisTemplate.scan(scanOptions);
//        while (cursor.hasNext()) {
//            String key = cursor.next();
//            Long ttl = redisTemplate.getExpire(key);
//            if (ttl <= 0) {
//                redisTemplate.delete(key);
//                System.out.println("Deleted expired key: " + key);
//            }
//        }
//        cursor.close();
//    }
}
