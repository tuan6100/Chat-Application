package com.chat.app.service.elasticsearch;

import com.chat.app.exception.ChatException;
import com.chat.app.model.elasticsearch.MessageIndex;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.elasticsearch.MessageSearchRepository;

import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageSearchService {

    @Autowired
    private MessageSearchRepository messageSearchRepository;

    @Autowired
    private MessageService messageService;


    @Async
    public void addNewMessage(Message message) {
        try {
            MessageIndex messageIndex = new MessageIndex(message.getMessageId(), message.getContent());
            System.out.println("Adding message: " + message.getContent() + " to elasticsearch");
            MessageIndex savedMessageIndex = messageSearchRepository.save(messageIndex);
            if (savedMessageIndex != null) {
                System.out.println("Message saved successfully: " + savedMessageIndex.getContent());
            } else {
                System.err.println("Failed to save message: " + message.getContent());
            }
        } catch (Exception e) {
            System.err.println("Failed to save message: " + e.getMessage());
        }
    }

    public List<Long> searchMessage(Long chatId, String content) throws ChatException {
        List<MessageIndex> messageIndices = messageSearchRepository.findByContent(content);
        List<Long> result = new ArrayList<>();
        for (MessageIndex messageIndex : messageIndices) {
            result.add(messageIndex.getMessageId());
        }
        return result;
    }

    @Async
    public void updateMessage(Long messageId, String content) {
        if (!messageSearchRepository.existsById(messageId)) {
            return;
        }
        MessageIndex messageIndex = messageSearchRepository.findById(messageId).get();
        messageIndex.setContent(content);
        System.out.println("Updating message: " + content + " to elasticsearch");
        messageSearchRepository.save(messageIndex);
    }

    @Async
    public void deleteMessage(Long messageId) {
        messageSearchRepository.deleteById(messageId);
    }



}
