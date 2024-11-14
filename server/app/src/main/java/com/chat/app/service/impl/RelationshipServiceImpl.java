package com.chat.app.service.impl;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.repository.RelationshipRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public void inviteFriend(Long userId, Long friendId) throws ChatException {
        Account user = accountService.findAccount(userId);
        Account friend = accountService.findAccount(friendId);
        if (relationshipRepository.findByUserAndFriend(user, friend) != null) {
            if (relationshipRepository.findByUserAndFriend(user, friend).getStatus() == RelationshipStatus.BLOCKED) {
                throw new ChatException("You are blocked by this user.");
            }
            throw new ChatException("You two are already friends.");
        }
        Relationship relationship = new Relationship(user, friend , RelationshipStatus.WAITING_TO_ACCEPT, new Date());
        relationshipRepository.save(relationship);
    }

    @Override
    public void acceptFriend(Long userId, Long friendId) throws ChatException {
        Account user = accountService.findAccount(userId);
        Account friend = accountService.findAccount(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        relationshipRepository.save(relationship);
    }

    @Override
    public void unfriend(Long userId, Long friendId) throws ChatException {
        Account user = accountService.findAccount(userId);
        Account friend = accountService.findAccount(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationshipRepository.delete(relationship);
    }

    @Override
    public void blockFriend(Long userId, Long friendId) throws ChatException {
        Account user = accountService.findAccount(userId);
        Account friend = accountService.findAccount(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationship.setStatus(RelationshipStatus.BLOCKED);
        relationshipRepository.save(relationship);
    }

    @Override
    public void unblockFriend(Long userId, Long friendId) throws ChatException {
        Account user = accountService.findAccount(userId);
        Account friend = accountService.findAccount(friendId);
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend);
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        relationshipRepository.save(relationship);
    }


}