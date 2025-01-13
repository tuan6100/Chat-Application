package com.chat.app.service.impl;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.payload.request.MessageVerifierRequest;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.GroupChatRepository;
import com.chat.app.repository.jpa.MessageRepository;
import com.chat.app.repository.jpa.PrivateChatRepository;
import com.chat.app.repository.redis.MessageCacheRepository;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import com.chat.app.service.redis.MessageCacheService;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
public class ChatServiceImpl implements ChatService {


    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    @Lazy
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageCacheRepository messageCacheRepository;

    @Autowired
    private MessageCacheService messageCacheService;

    @Override
    public Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    @Override
    public List<MessageResponse> getMessages(Long chatId, Long accountId, int page, int size) {
        if (messageCacheService.existsInCache(accountId, chatId)) {
            List<MessageResponse> messages = messageCacheService.getMessagesFromCache(accountId, chatId, page, size);
            messageCacheService.cacheNextMessages(chatId, accountId, page, size);
            return messages;
        }
        Pageable pageable = PageRequest.of(page, 20);
        Page<Message> messages = chatRepository.findLatestMessagesByChatId(chatId, pageable);
        List<MessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messages) {
            messageResponses.add(MessageResponse.fromEntity(message));
        }
        Collections.reverse(messageResponses);
        messageCacheService.setCache(chatId, accountId, messageResponses);
        return messageResponses;
    }

    @Override
    public void verifyMessage(Long chatId, MessageVerifierRequest request) {
        try {
            Message message = messageRepository.findByRandomId(request.getRandomId());
            if (message == null) {
                return;
            }
            System.out.println("Verifying message: " + message.getContent());
            if (request.getStatus().equals("sent")) {
                message.setRandomId(null);
                message = messageRepository.save(message);
                MessageResponse messageResponse = MessageResponse.fromEntity(message);
                List<Long> membersInChat = getAllMembersInChat(chatId);
                messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
            } else if (request.getStatus().equals("failed")) {
                messageRepository.delete(message);
            }
        } catch (OptimisticLockException | StaleObjectStateException e) {
            System.err.println("Optimistic lock exception");
        }
    }

    @Override
    public Chat addMessage(Long chatId, Message message) throws ChatException {
        Chat chat = getChat(chatId);
        chat.getMessages().add(message);
        return chatRepository.save(chat);
    }

    @Override
    public Chat pinMessage(Long messageId) throws ChatException {
        Message message = messageService.getMessage(messageId);
        Chat chat = message.getChat();
        chat.getPinnedMessages().add(message);
        return chatRepository.save(chat);
    }

    @Override
    public void removeMessage(Long messageId) throws ChatException {
        Message message = messageService.getMessage(messageId);
        Chat chat = message.getChat();
        chat.getMessages().remove(message);
        chatRepository.save(chat);
    }

    @Override
    public Chat changeTheme(Long chatId, Theme theme) throws ChatException {
        Chat chat = getChat(chatId);
        chat.setTheme(theme);
        return chatRepository.save(chat);
    }

    @Override
    public List<Long> getAllMembersInChat(Long chatId) {
        if (privateChatRepository.findById(chatId).isPresent()) {
            PrivateChat privateChat = privateChatRepository.findById(chatId).get();
            Relationship relationship = privateChat.getRelationship();
            return List.of(relationship.getFirstAccount().getAccountId(), relationship.getSecondAccount().getAccountId());
        } else if (groupChatRepository.findById(chatId).isPresent()) {
            GroupChat groupChat = groupChatRepository.findById(chatId).get();
            List<Account> members = groupChat.getMembers().stream().toList();
            List<Long> memberIds = new ArrayList<>();
            for (Account member : members) {
                memberIds.add(member.getAccountId());
            }
            return memberIds;
        }
        return null;
    }

    @Override
    public Message getLastestMessage(Long chatId) {
        return chatRepository.findLatestMessageByChatId(chatId);
    }


}
