package com.chat.app.service.interfaces.message.caching;

import com.chat.app.payload.response.MessageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageLocalCacheService extends MessageCacheService {

    void putMessagesToTheTop(Long chatId, List<MessageResponse> messageResponseList);

    void putMessagesToTheBottom(Long chatId, List<MessageResponse> messageResponseList);
}
