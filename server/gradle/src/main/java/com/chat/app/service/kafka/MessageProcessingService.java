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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    private AccountService accountService;

    @Autowired
    @Lazy
    private MessageCacheService messageCacheService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processSendMessage(ChatMessageRequest chatMessage) throws ChatException {
        Long chatId = chatMessage.getChatId();
        MessageRequest messageRequest = chatMessage.getMessageRequest();
        Chat chat = chatService.getChat(chatId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        MessageType type = MessageType.valueOf(messageRequest.getType().toUpperCase());
        Date sentTime = chatMessage.getMessageRequest().getSentTime();
        Message message = new Message(sender, messageRequest.getContent(), type, sentTime, chat);
        message = messageRepository.save(message);
        messageCacheService.cacheNewMessage(chatId, sender.getAccountId(), message);
        messagingTemplate.convertAndSend("/client/chat/" + chatId, MessageResponse.fromEntity(message));
    }

    @Transactional
    public void processReplyMessage(Long chatId, MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.getChat(chatId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        MessageType type = MessageType.valueOf(String.valueOf(messageRequest.getType()));
        Message repliedMessage = messageService.getMessage(messageRequest.getRepliedMessageId());
        Date sentTime = messageRequest.getSentTime();
        Message newMessage = new Message(sender, messageRequest.getContent(), type, sentTime, chat);
        newMessage.getRepliedMessages().add(repliedMessage);
        newMessage = messageRepository.save(newMessage);
        messageCacheService.cacheNewMessage(chatId, sender.getAccountId(), newMessage);
        messagingTemplate.convertAndSend("/client/chat/" + chatId, MessageResponse.fromEntity(newMessage));
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
        messageCacheService.cacheNewMessage(message.getChat().getChatId(), message.getSender().getAccountId(), message);
        messagingTemplate.convertAndSend("/client/chat/" + message.getChat().getChatId(), MessageResponse.fromEntity(message));
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
        messageCacheService.cacheNewMessage(message.getChat().getChatId(), message.getSender().getAccountId(), message);
        messagingTemplate.convertAndSend("/client/chat/" + message.getChat().getChatId(), MessageResponse.fromEntity(message));
    }
}
