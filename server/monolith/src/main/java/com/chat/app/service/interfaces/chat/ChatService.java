package com.chat.app.service.interfaces.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.payload.response.MessageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

     Chat getChat(Long chatId);

     <C extends Chat> C getChatType(Long chatId);

     List<MessageResponse> getMessagesByPage(Long chatId, int page, int size);

     MessageResponse getLastestMessage(Long chatId);

     int getMaxPageOfMessages(Long chatId, int size);

     List<MessageResponse> getPinnedMessages(Long chatId);

     List<MessageResponse> getLinkMessages(Long chatId);

     List<MessageResponse> getMediaMessages(Long chatId);

     void deleteMessage(Long MessageId) throws ChatException;

     Chat changeTheme(Long chatId, Theme theme);

     List<ChatResponse> getAllChatsByAccountId(Long accountId) throws ChatException;

}
