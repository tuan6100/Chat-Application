package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RelationshipService {

    public Relationship getRelationship(Long relationshipId) throws ChatException;

    public Map<Account, String> findFriends(Long myAccountId, String username) throws ChatException;

    public Relationship inviteFriend(Long userId, Long friendId) throws ChatException;
    
    public Relationship acceptFriend(Long userId, Long friendId) throws ChatException;

    public void unfriend(Long userId, Long friendId) throws ChatException;
    
    public Relationship blockFriend(Long userId, Long friendId) throws ChatException;
    
    public Relationship unblockFriend(Long userId, Long friendId) throws ChatException;
}
