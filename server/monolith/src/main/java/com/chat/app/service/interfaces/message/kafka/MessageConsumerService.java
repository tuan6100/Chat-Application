package com.chat.app.service.interfaces.message.kafka;

import com.chat.app.dto.ChatMessage;
import com.chat.app.payload.request.MessageSeenRequest;
import com.chat.app.payload.request.MessageUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface MessageConsumerService {

    void consumeMessageSent(ChatMessage chatMessage);

    void consumeMessageConfirmation(ChatMessage chatMessage);

    void consumerMessageMarkedAsViewed(ChatMessage chatMessage);

    void consumeMessageUpdated(ChatMessage chatMessage);

    void consumeMessageDeleted(ChatMessage chatMessage);

    void consumeMessageRestored(ChatMessage chatMessage);
}
