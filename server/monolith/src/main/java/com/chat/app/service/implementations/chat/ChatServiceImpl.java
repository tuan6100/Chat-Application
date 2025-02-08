package com.chat.app.service.implementations.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.*;
import com.chat.app.service.interfaces.chat.ChatService;
import com.chat.app.service.interfaces.chat.GroupChatService;
import com.chat.app.service.interfaces.chat.PrivateChatService;
import com.chat.app.service.interfaces.message.MessageSearchService;
import com.chat.app.service.interfaces.message.caching.MessageLocalCacheService;
import com.chat.app.service.interfaces.message.caching.MessageRedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@SuppressWarnings("unchecked")
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private CloudStorageRepository cloudStorageRepository;

    @Autowired
    private SpamChatRepository spamChatRepository;

    @Autowired
    @Lazy
    private MessageSearchService messageSearchService;

    @Autowired
    private MessageRedisCacheService messageRedisCacheService;

    @Autowired
    private MessageLocalCacheService messageLocalCacheService;

    @Autowired
    @Lazy
    private PrivateChatService privateChatService;

    @Autowired
    @Lazy
    private GroupChatService groupChatService;


    @Override
    public Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    @Override
    public <C extends Chat> C getChatType(Long chatId) {
        if (privateChatRepository.findById(chatId).isPresent()) {
            return (C) privateChatRepository.findById(chatId).get();
        }
        if (groupChatRepository.findById(chatId).isPresent()) {
            return (C) groupChatRepository.findById(chatId).get();
        }
        if (cloudStorageRepository.findById(chatId).isPresent()) {
            return (C) cloudStorageRepository.findById(chatId).get();
        }
        if (spamChatRepository.findById(chatId).isPresent()) {
            return (C) spamChatRepository.findById(chatId).get();
        }
        return null;
    }

    @Override
    public List<MessageResponse> getMessagesByPage(Long chatId, int page, int size) {
        List<MessageResponse> localCache = messageLocalCacheService.getMessagesFromCache(chatId, page, size);
        if (localCache  != null && !localCache .isEmpty()) {
            System.out.println("Local Cache hit");
            return localCache;
        }
        List<MessageResponse> redisCache = messageRedisCacheService.getMessagesFromCache(chatId, page, size);
        if (redisCache != null && !redisCache.isEmpty()) {
            messageLocalCacheService.setCache(chatId, redisCache, page);
            System.out.println("Redis Cache hit");
            return redisCache;
        }
        Pageable pageable = PageRequest.of(page, size);
        List<Message> messages = chatRepository.findMostRecentMessagesByChatId(chatId, pageable).getContent();
        List<MessageResponse> messageResponses = messages.stream().parallel()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
        Collections.reverse(messageResponses);
        messageLocalCacheService.putMessagesToTheTop(chatId, messageResponses);
        return messageResponses;
    }

    @Override
    public MessageResponse getLastestMessage(Long chatId) {
        return MessageResponse.fromEntity(chatRepository.findLatestMessageByChatId(chatId));
    }

    @Override
    public int getMaxPageOfMessages(Long chatId, int size) {
        return (int) Math.ceil(chatRepository.countMessagesByChatId(chatId) / (double) size);
    }

    @Override
    public List<MessageResponse> getPinnedMessages(Long chatId) {
        return List.of();
    }

    @Override
    public List<MessageResponse> getLinkMessages(Long chatId) {
        return List.of();
    }

    @Override
    public List<MessageResponse> getMediaMessages(Long chatId) {
        return List.of();
    }

    @Override
    public void deleteMessage(Long messageId) throws ChatException {
        Message message = messageSearchService.searchMessageById(messageId);
        Chat chat = message.getChat();
        chat.getMessages().remove(message);
        chatRepository.save(chat);
    }

//    public void deleteMessage(Long chatId, Long messageId) throws ChatException {
//        Chat chat = getChat(chatId);
//        chat.getMostRecentMessages().removeIf(message -> message.getMessageId().equals(messageId));
//        chatRepository.save(chat);
//    }

    @Override
    public Chat changeTheme(Long chatId, Theme theme) {
        Chat chat = getChat(chatId);
        chat.setTheme(theme);
        return chatRepository.save(chat);
    }

    @Override
    public List<ChatResponse> getAllChatsByAccountId(Long accountId) throws ChatException {
        return Stream.concat(privateChatService.getAllPrivateChatsByAccountId(accountId).stream(),
                        groupChatService.getAllGroupChatsByMemberId(accountId).stream())
                .toList();
    }

}
