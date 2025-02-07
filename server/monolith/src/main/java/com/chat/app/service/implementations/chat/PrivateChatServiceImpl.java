package com.chat.app.service.implementations.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.PrivateChatRepository;
import com.chat.app.service.interfaces.chat.PrivateChatService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.relationship.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrivateChatServiceImpl extends ChatServiceImpl implements PrivateChatService {

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private RelationshipService relationshipService;


    @Override
    public PrivateChat getChat(Long chatId) {
        return privateChatRepository.findById(chatId).orElse(null);
    }

    @Override
    public PrivateChat create(Theme theme, Long RelationshipId) throws ChatException {
        Relationship relationship = relationshipService.getRelationship(RelationshipId);
        if (relationship == null) {
            throw new ChatException("Relationship not found");
        }
        PrivateChat privateChat = new PrivateChat(theme, relationship);
        return chatRepository.save(privateChat);
    }

    @Override
    public List<ChatResponse> getAllPrivateChatsByAccountId(Long accountId) throws ChatException {
        Account account = accountSearchService.searchAccountById(accountId);
        List<Long> privateChatIds =  privateChatRepository.findPrivateChatsByAccountId(accountId);
        return privateChatIds.stream().parallel()
            .map(chatId -> new ChatResponse(chatId, account.getUsername(), account.getAvatar(),
                    ChatResponse.LatestMessage.fromResponse(accountId, getLastestMessage(chatId))))
                .toList();

    }

    @Override
    public Long getByRelationshipId(Long relationshipId) {
        return privateChatRepository.findByRelationshipId(relationshipId);
    }

    @Override
    public void remove(Long chatId) {
        chatRepository.deleteById(chatId);
    }
}
