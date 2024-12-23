package com.chat.app.service.impl;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.SpamChat;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.SpamChatRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.SpamChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SpamChatServiceImpl extends ChatServiceImpl implements SpamChatService {

    @Autowired
    private SpamChatRepository spamChatRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    private AccountService accountService;


    @Override
    public Long getSpamChatId(Long senderId, Long receiverId) {
        return spamChatRepository.findSpamChatBySenderAndReceiver(senderId, receiverId);
    }

    @Override
    public SpamChat create(Theme theme, Long SenderId, Long ReceiverId) throws ChatException {
        SpamChat spamChat = new SpamChat(theme, accountService.getAccount(SenderId), accountService.getAccount(ReceiverId));
        return chatRepository.save(spamChat);
    }

    @Override
    public Chat addMessage(Long chatId, Message message) throws ChatException {
        Chat chat = getChat(chatId);
        if (!(chat instanceof SpamChat)) {
            throw new ChatException("Chat is not a spam chat");
        }
        if (chat.getMessages().size() >= 10) {
            throw new ChatException("You need make friend with this user to send more messages");
        }
        return super.addMessage(chatId, message);
    }

    @Override
    public void remove(Long chatId) {
        chatRepository.deleteById(chatId);
    }
}
