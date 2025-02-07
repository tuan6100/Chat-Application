package com.chat.app.service.interfaces.message;

import com.chat.app.exception.ChatException;
import com.chat.app.model.elasticsearch.MessageIndex;
import com.chat.app.model.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageSearchService {

    Message searchMessageById(Long messageId) throws ChatException;

    List<Long> searchMessagesByKeyword(Long chatId, String keyword);

    List<MessageIndex> filterMessagesByKeyword(Long chatId, String keyword);

}
