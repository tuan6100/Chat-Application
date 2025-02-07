package com.chat.app.dto;

import com.chat.app.payload.request.MessageConfirmationRequest;
import com.chat.app.payload.request.MessageRequest;
import lombok.Data;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ChatMessage {

    private Long chatId;
    private MessageRequest messageRequest;
    private MessageConfirmationRequest messageConfirmationRequest;
    private AtomicInteger retryCount = new AtomicInteger(0);


    public ChatMessage(Long chatId, MessageRequest messageRequest) {
        this.chatId = chatId;
        this.messageRequest = messageRequest;
    }

    public ChatMessage(Long chatId, MessageConfirmationRequest messageConfirmationRequest) {
        this.chatId = chatId;
        this.messageConfirmationRequest = messageConfirmationRequest;
    }

    public void incrementRetryCount() {
        retryCount.incrementAndGet();
    }

    public boolean isRetryLimitExceeded() {
        return retryCount.get() > 2;
    }
}
