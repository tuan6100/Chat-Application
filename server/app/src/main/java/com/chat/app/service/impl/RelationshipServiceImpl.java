package com.chat.app.service.impl;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.enumeration.Theme;
import com.chat.app.exception.AccountException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chatroom.PrivateChat;
import com.chat.app.repository.AccountRepository;
import com.chat.app.repository.ChatRoomRepository;
import com.chat.app.repository.RelationshipRepository;
import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Override
    public void inviteFriend(Long userId, Long friendId) throws AccountException {
        Account user = accountRepository.findByAccountId(userId);
        Account friend = accountRepository.findByAccountId(friendId);
        if (relationshipRepository.findByUserAndFriend(user, friend) != null) {
            if (relationshipRepository.findByUserAndFriend(user, friend).getStatus() == RelationshipStatus.BLOCKED) {
                throw new AccountException("You are blocked by this user.");
            }
            throw new AccountException("You two are already friends.");
        }
        Relationship relationship = new Relationship(user, friend , RelationshipStatus.WAITING_TO_ACCEPT, new Date());
        chatRoomRepository.save(new PrivateChat(friend.getUsername(), friend.getAvatarImagePath(), Theme.SYSTEM, relationship));
        relationshipRepository.save(relationship);
    }

    @Override
    public void acceptFriend(Long userId, Long friendId) {
        Account user = accountRepository.findByAccountId(userId);
        Account friend = accountRepository.findByAccountId(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        relationshipRepository.save(relationship);
    }

    @Override
    public void unfriend(Long userId, Long friendId) {
        Account user = accountRepository.findByAccountId(userId);
        Account friend = accountRepository.findByAccountId(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationshipRepository.delete(relationship);
    }

    @Override
    public void blockFriend(Long userId, Long friendId) {
        Account user = accountRepository.findByAccountId(userId);
        Account friend = accountRepository.findByAccountId(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationship.setStatus(RelationshipStatus.BLOCKED);
        relationshipRepository.save(relationship);
    }

    @Override
    public void unblockFriend(Long userId, Long friendId) {
        Account user = accountRepository.findByAccountId(userId);
        Account friend = accountRepository.findByAccountId(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        relationshipRepository.save(relationship);
    }


}