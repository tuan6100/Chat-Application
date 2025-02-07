package com.chat.app.service.implementations.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.SpamChat;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.SpamChatRepository;
import com.chat.app.service.interfaces.chat.SpamChatService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.relationship.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpamChatServiceImpl extends ChatServiceImpl implements SpamChatService {

    @Autowired
    private SpamChatRepository spamChatRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    private AccountSearchService accountSearchService;

    @Autowired
    @Lazy
    private RelationshipService relationshipService;


    @Override
    public SpamChat getSpamChat(Long senderId, Long receiverId) {
        return spamChatRepository.findSpamChatBySenderAndReceiver(senderId, receiverId);
    }

    @Override
    public SpamChat create(Theme theme, Long SenderId, Long ReceiverId) throws ChatException {
        SpamChat spamChat = new SpamChat(theme, accountSearchService.searchAccountById(SenderId), accountSearchService.searchAccountById(ReceiverId));
        return chatRepository.save(spamChat);
    }

    @Scheduled(fixedRate = 2592000000L)
    @Override
    public void removeSpamChats() {
        List<Chat> Chats = spamChatRepository.findAll();
        for (Chat chat : Chats) {
            if (chat instanceof SpamChat spamChat) {
                spamChatRepository.delete(spamChat);
            }
        }
    }
}
