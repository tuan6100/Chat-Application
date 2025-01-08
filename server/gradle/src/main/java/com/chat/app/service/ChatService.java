package com.chat.app.service;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.payload.request.MessageVerifierRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

     Chat getChat(Long chatId) throws ChatException;

     List<MessageResponse> getMessages(Long chatId, Long accountId, int page, int size);

     void verifyMessage(Long chatId, Long accountId, MessageVerifierRequest request) throws ChatException;

     Chat addMessage(Long chatId, Message message) throws ChatException;

     Chat pinMessage(Long messageId) throws ChatException;

     void removeMessage(Long MessageId) throws ChatException;

     Chat changeTheme(Long chatId, Theme theme) throws ChatException;

     List<Long> getAllMembersInChat(Long chatId);


}
