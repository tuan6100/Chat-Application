package com.chat.app.service.kafka;

import com.chat.app.enumeration.MessageType;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.ChatMessageRequest;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.MessageRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import com.chat.app.service.redis.MessageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MessageProcessingService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    @Lazy
    private ChatService chatService;

    @Autowired
    @Lazy
    private MessageCacheService messageCacheService;

    @Autowired
    @Lazy
    private AccountService accountService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;


    @Transactional
    public void processSendMessage(ChatMessageRequest chatMessage) throws ChatException {
        Long chatId = chatMessage.getChatId();
        MessageRequest messageRequest = chatMessage.getMessageRequest();
        String randomId = messageRequest.getRandomId();
        if (messageRepository.findByRandomId(randomId) != null) {
            System.out.println("Delay processing message: " + messageRequest.getContent());
            kafkaTemplate.send("send-message", chatMessage);
            return;
        }
        Chat chat = chatService.getChat(chatId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        MessageType type = MessageType.valueOf(messageRequest.getType().toUpperCase());
        Date sentTime = chatMessage.getMessageRequest().getSentTime();
        Message message = new Message(messageRequest.getRandomId(), sender, messageRequest.getContent(), type, sentTime, chat);
        message = messageRepository.save(message);
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        messagingTemplate.convertAndSend("/client/chat/" + chatId, messageResponse);
    }

    @Transactional
    public void processEditMessage(Long messageId, MessageRequest messageRequest) throws ChatException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ChatException("Message not found"));
        chatService.removeMessage(messageId);
        message.setContent(messageRequest.getContent());
        message.setType(MessageType.valueOf(String.valueOf(messageRequest.getType())));
        message.setSentTime(messageRequest.getSentTime());
        message = messageRepository.save(message);
        Long chatId = message.getChat().getChatId();
        List<Long> membersInChat = chatService.getAllMembersInChat(chatId);
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
        messagingTemplate.convertAndSend("/client/chat/" + chatId, messageResponse);
    }

    @Transactional
    public void processUnsendMessage(String messageId) throws ChatException {
        Message message = messageService.getMessage(Long.valueOf(messageId));
        message.setUnsend(true);
        chatService.removeMessage(Long.valueOf(messageId));
        messageRepository.save(message);
        messageCacheService.removeMessageFromCache(message.getChat().getChatId(), message.getSender().getAccountId(), Long.valueOf(messageId));
        messagingTemplate.convertAndSend("/client/chat/" + message.getChat().getChatId(), "unsend " + messageId);
    }

    @Transactional
    public void processRestoreMessage(String messageId) throws ChatException {
        Message message = messageService.getMessage(Long.valueOf(messageId));
        message.setUnsend(false);
        chatService.addMessage(message.getChat().getChatId(), message);
        messageRepository.save(message);
        Long chatId = message.getChat().getChatId();
        List<Long> membersInChat = chatService.getAllMembersInChat(chatId);
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
        messagingTemplate.convertAndSend("/client/chat/" + chatId, messageResponse);
    }
}
