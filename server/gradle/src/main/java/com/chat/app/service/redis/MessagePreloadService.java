//package com.chat.app.service.redis;
//
//import com.chat.app.repository.jpa.ChatRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MessagePreloadService {
//
//    @Autowired
//    private ChatRepository chatRepository;
//
//    @Autowired
//    private MessageCacheService messageCacheService;
//
//    @Value("${spring.redis.cache.messages-per-chat.size}")
//    private int size;
//
//
//    @PostConstruct
//    public void preloadCacheOnStartup(Long chatId, Long accountId) {
//        int page = chatRepository.countMessagesByChatId(chatId) / size;
//        messageCacheService.cacheNextMessages(chatId, accountId, page, size);
//    }
//}
