package com.chat.app.service.interfaces.chat;

import com.chat.app.exception.ChatException;
import com.chat.app.payload.request.NewMessageRequest;
import com.chat.app.payload.response.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public interface CloudStorageService {

    ChatResponse getCloudStorageByAccountId(Long accountId) throws ChatException;

    void checkFullStorage(Long accountId, NewMessageRequest messageRequest) throws ChatException;

    void getMoreStorage(Long accountId, Long storageSize);

}
