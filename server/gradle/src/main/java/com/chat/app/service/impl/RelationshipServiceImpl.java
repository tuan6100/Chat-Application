package com.chat.app.service.impl;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.dto.FriendStatusDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.payload.response.RelationshipResponse;
import com.chat.app.repository.jpa.RelationshipRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.PrivateChatService;
import com.chat.app.service.RelationshipService;
import com.chat.app.service.SpamChatService;
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

    @Lazy
    @Autowired
    private PrivateChatService privateChatService;

    @Lazy
    @Autowired
    private SpamChatService spamChatService;


    @Override
    public Relationship getRelationship(Long relationshipId){
        Optional<Relationship> relationship =  relationshipRepository.findById(relationshipId);
        return relationship.orElse(null);
    }


    @Override
    public Long getRelationshipId(Long firstAccountId, Long secondAccountId) {
        return relationshipRepository.findByFirstAccountAndSecondAccount(firstAccountId, secondAccountId);
    }

    @Override
    public RelationshipResponse getRelationshipStatus(Long firstAccountId, Long secondAccountId)  {
        Long relationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(firstAccountId, secondAccountId);
        Long reverseRelationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(secondAccountId, firstAccountId);
        Relationship relationship = relationshipId != null ? getRelationship(relationshipId) : (reverseRelationshipId != null ? getRelationship(reverseRelationshipId) : null);
        if (relationship != null) {
            if (relationship.getStatus() == RelationshipStatus.ACCEPTED) {
                Long chatId = privateChatService.getByRelationshipId(relationship.getRelationshipId());
                return new RelationshipResponse(firstAccountId, secondAccountId, "FRIEND", chatId);
            }
            if (relationship.getStatus() == RelationshipStatus.WAITING_TO_ACCEPT) {
                if (relationshipId != null) {
                    return new RelationshipResponse(firstAccountId, secondAccountId, "WAITING FOR ACCEPTANCE", null);
                }
                return new RelationshipResponse(firstAccountId, secondAccountId, "NEW FRIEND REQUEST", null);
            }
            if (relationship.getStatus() == RelationshipStatus.BLOCKED) {
                if (relationshipId != null) {
                    return new RelationshipResponse(firstAccountId, secondAccountId, "BLOCKED", null);
                }
                return new RelationshipResponse(firstAccountId, secondAccountId, "BLOCKED BY USER", null);
            }
        }
        return new RelationshipResponse(firstAccountId, secondAccountId, "NO RELATIONSHIP", null);
    }

    @Override
    public void inviteFriend(Long userId, Long friendId) throws ChatException {
        Long existingRelationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(userId, friendId);
        Relationship existingRelationship = existingRelationshipId != null ? getRelationship(existingRelationshipId) : null;
        if (existingRelationship != null) {
            switch (existingRelationship.getStatus()) {
                case ACCEPTED:
                    throw new ChatException("You are already friends.");
                case WAITING_TO_ACCEPT:
                    throw new ChatException("Invitation already sent.");
                case BLOCKED:
                    throw new ChatException("You are blocked by this user.");
            }
        }
        Account user = accountService.getAccount(userId);
        Account friend = accountService.getAccount(friendId);
        Relationship relationship = new Relationship(user, friend, RelationshipStatus.WAITING_TO_ACCEPT, new Date());
        relationshipRepository.save(relationship);
    }

    @Override
    public void acceptFriend(Long userId, Long friendId) throws ChatException {
        Long relationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(friendId, userId);
        Relationship relationship = relationshipId != null ? getRelationship(relationshipId) : null;
        if (relationship == null || relationship.getStatus() != RelationshipStatus.WAITING_TO_ACCEPT) {
            throw new ChatException("No pending invitation found.");
        }
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        privateChatService.create(Theme.SYSTEM, relationshipId);
        relationshipRepository.save(relationship);
    }

    @Override
    public List<AccountResponse> getFriendsList(Long userId) {
        List<Relationship> relationships = relationshipRepository.findByAccountAndStatus(userId, RelationshipStatus.ACCEPTED);
        return relationships.stream()
                .map(rel -> {
                    Account friend = rel.getFirstAccount().getAccountId().equals(userId) ? rel.getSecondAccount() : rel.getFirstAccount();
                    return new AccountResponse(friend.getAccountId(), friend.getUsername(), friend.getAvatar());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void rejectFriend(Long userId, Long friendId) throws ChatException {
        Long relationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(friendId, userId);
        Relationship relationship = relationshipId != null ? getRelationship(relationshipId) : null;
        if (relationship == null || relationship.getStatus() != RelationshipStatus.WAITING_TO_ACCEPT) {
            throw new ChatException("No pending invitation found.");
        }
        relationshipRepository.delete(relationship);
    }

    @Override
    public void unfriend(Long userId, Long friendId) throws ChatException {
        Long relationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(userId, friendId);
        Long reverseRelationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(friendId, userId);
        Relationship relationship = relationshipId != null ? getRelationship(relationshipId) : (reverseRelationshipId != null ? getRelationship(reverseRelationshipId) : null);
        if (relationship == null) {
            throw new ChatException("You can't do this.");
        }
        relationshipRepository.delete(relationship);
    }

    @Override
    public void blockUser(Long userId, Long friendId) throws ChatException {
        Long relationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(userId, friendId);
        Long reverseRelationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(friendId, userId);
        Relationship relationship = relationshipId != null ? getRelationship(relationshipId) : (reverseRelationshipId != null ? getRelationship(reverseRelationshipId) : null);
        if (relationship == null) {
            Account user = accountService.getAccount(userId);
            Account friend = accountService.getAccount(friendId);
            relationship = new Relationship(user, friend, RelationshipStatus.BLOCKED, new Date());
        } else {
            relationship.setStatus(RelationshipStatus.BLOCKED);
        }
        relationshipRepository.save(relationship);
    }

    @Override
    public void unblockUser(Long userId, Long friendId) throws ChatException {
        Long relationshipId = relationshipRepository.findByFirstAccountAndSecondAccount(userId, friendId);
        Relationship relationship = relationshipId != null ? getRelationship(relationshipId) : null;
        if (relationship == null || relationship.getStatus() != RelationshipStatus.BLOCKED) {
            throw new ChatException("This relationship is not blocked.");
        }
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        relationshipRepository.save(relationship);
    }

    @Override
    public List<FriendStatusDTO> getInvitationsList(Long userId) {
        List<Relationship> invitations = relationshipRepository.findByAccountAndStatus(userId, RelationshipStatus.WAITING_TO_ACCEPT);
        return invitations.stream()
                .filter(rel -> !rel.getFirstAccount().getAccountId().equals(userId))
                .map(rel -> new FriendStatusDTO(rel.getFirstAccount().getAccountId(), rel.getStatus().name()))
                .collect(Collectors.toList());
    }


}
