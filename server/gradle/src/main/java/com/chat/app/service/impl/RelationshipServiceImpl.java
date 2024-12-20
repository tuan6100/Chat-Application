package com.chat.app.service.impl;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.FriendStatusDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.repository.jpa.RelationshipRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.PrivateChatService;
import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Lazy
    @Autowired
    private AccountService accountService;

    @Autowired
    private PrivateChatService privateChatService;

    @Override
    public Relationship getRelationship(Long relationshipId) throws ChatException {
        return relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new ChatException("No relationship found."));
    }

    @Override
    public Relationship getRelationship(Long firstAccountId, Long secondAccountId) throws ChatException {
        Relationship relationship = relationshipRepository.findByFirstAccountAndSecondAccount(firstAccountId, secondAccountId);
        if (Objects.equals(firstAccountId, secondAccountId)) {
            throw new ChatException("You cannot be friends with yourself.");
        }
        if (relationship == null) {
            throw new ChatException("No relationship found.");
        }
        return relationship;
    }

    @Override
    public Relationship inviteFriend(Long userId, Long friendId) throws ChatException {
        Relationship existingRelationship = relationshipRepository.findByFirstAccountAndSecondAccount(userId, friendId);
        if (existingRelationship != null) {
            switch (existingRelationship.getStatus()) {
                case ACCEPTED:
                    throw new ChatException("You are already friends.");
                case WAITING_TO_ACCEPT:
                    throw new ChatException("Invitation already sent.");
                case BLOCKED:
                    throw new ChatException("This relationship is blocked.");
            }
        }

        Account user = accountService.getAccount(userId);
        Account friend = accountService.getAccount(friendId);
        Relationship relationship = new Relationship(user, friend, RelationshipStatus.WAITING_TO_ACCEPT, new Date());
        return relationshipRepository.save(relationship);
    }

    @Override
    public Relationship acceptFriend(Long userId, Long friendId) throws ChatException {
        Relationship relationship = getRelationship(friendId, userId); // Người mời là firstAccount
        if (relationship == null || relationship.getStatus() != RelationshipStatus.WAITING_TO_ACCEPT) {
            throw new ChatException("No pending invitation found.");
        }

        relationship.setStatus(RelationshipStatus.ACCEPTED);
        privateChatService.createPrivateChat(relationship);
        return relationshipRepository.save(relationship);
    }

    @Override
    public List<AccountResponse.FriendResponse> getFriendsList(Long userId) throws ChatException {
        List<Relationship> relationships = relationshipRepository.findByAccountAndStatus(userId, RelationshipStatus.ACCEPTED);
        return relationships.stream()
                .map(rel -> {
                    Account friend = rel.getFirstAccount().getAccountId().equals(userId) ? rel.getSecondAccount() : rel.getFirstAccount();
                    return new AccountResponse.FriendResponse(friend.getAccountId(), friend.getUsername(), friend.getAvatar(), friend.getStatus());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void refuseFriend(Long userId, Long friendId) throws ChatException {
        Relationship relationship = getRelationship(friendId, userId);
        if (relationship == null || relationship.getStatus() != RelationshipStatus.WAITING_TO_ACCEPT) {
            throw new ChatException("No pending invitation found.");
        }
        relationshipRepository.delete(relationship);
    }

    @Override
    public void unfriend(Long userId, Long friendId) throws ChatException {
        Relationship relationship = getRelationship(userId, friendId);
        if (relationship == null || relationship.getStatus() != RelationshipStatus.ACCEPTED) {
            throw new ChatException("You are not friends.");
        }
        relationshipRepository.delete(relationship);
        privateChatService.removePrivateChat(userId, friendId);
    }

    @Override
    public Relationship blockFriend(Long userId, Long friendId) throws ChatException {
        Relationship relationship = getRelationship(userId, friendId);
        if (relationship == null) {
            Account user = accountService.getAccount(userId);
            Account friend = accountService.getAccount(friendId);
            relationship = new Relationship(user, friend, RelationshipStatus.BLOCKED, new Date());
        } else {
            relationship.setStatus(RelationshipStatus.BLOCKED);
        }
        return relationshipRepository.save(relationship);
    }

    @Override
    public Relationship unblockFriend(Long userId, Long friendId) throws ChatException {
        Relationship relationship = getRelationship(userId, friendId);
        if (relationship == null || relationship.getStatus() != RelationshipStatus.BLOCKED) {
            throw new ChatException("This relationship is not blocked.");
        }
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        return relationshipRepository.save(relationship);
    }

    @Override
    public List<FriendStatusDTO> getInvitationsList(Long userId) throws ChatException {
        List<Relationship> invitations = relationshipRepository.findByAccountAndStatus(userId, RelationshipStatus.WAITING_TO_ACCEPT);
        return invitations.stream()
                .filter(rel -> !rel.getFirstAccount().getAccountId().equals(userId))
                .map(rel -> new FriendStatusDTO(rel.getFirstAccount().getAccountId(), rel.getStatus().name()))
                .collect(Collectors.toList());
    }


}
