package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.FriendStatusDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.payload.response.AccountResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RelationshipService {

    public Relationship getRelationship(Long relationshipId) throws ChatException;

    public Relationship getRelationship(Long firstAccountId, Long secondAccountId) throws ChatException;

    public Relationship inviteFriend(Long userId, Long friendId) throws ChatException;

    public List<FriendStatusDTO> getInvitationsList(Long userId) throws ChatException;

    public Relationship acceptFriend(Long userId, Long friendId) throws ChatException;

    public List<AccountResponse.FriendResponse> getFriendsList(Long userId) throws ChatException;

    public void refuseFriend(Long userId, Long friendId) throws ChatException;

    public void unfriend(Long userId, Long friendId) throws ChatException;
    
    public Relationship blockFriend(Long userId, Long friendId) throws ChatException;
    
    public Relationship unblockFriend(Long userId, Long friendId) throws ChatException;
}
