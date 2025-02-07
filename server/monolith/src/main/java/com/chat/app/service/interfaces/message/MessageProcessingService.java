package com.chat.app.service.interfaces.message;

import com.chat.app.dto.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface MessageProcessingService {

    void processMessageSent(ChatMessage chatMessage);

    void processMessageConfirmation(ChatMessage chatMessage);

    void processMessageMarkedAsViewed(ChatMessage chatMessage);

    void processMessageUpdated(ChatMessage chatMessage);

    void processMessageDeleted(ChatMessage chatMessage);

    void processMessageRestored(ChatMessage chatMessage);

}
