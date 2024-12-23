package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.FriendStatusDTO;
import com.chat.app.model.entity.Relationship;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.payload.response.RelationshipResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RelationshipService {

    public Relationship getRelationship(Long relationshipId) throws ChatException;

    public Long getRelationshipId(Long firstAccountId, Long secondAccountId) throws ChatException;

    public RelationshipResponse getRelationshipStatus(Long firstAccountId, Long secondAccountId) throws ChatException ;

    public void inviteFriend(Long userId, Long friendId) throws ChatException;

    public List<FriendStatusDTO> getInvitationsList(Long userId);

    public void acceptFriend(Long userId, Long friendId) throws ChatException;

    public List<AccountResponse.FriendResponse> getFriendsList(Long userId);

    public void refuseFriend(Long userId, Long friendId) throws ChatException;

    public void unfriend(Long userId, Long friendId) throws ChatException;
    
    public void blockFriend(Long userId, Long friendId) throws ChatException;
    
    public void unblockFriend(Long userId, Long friendId) throws ChatException;
}
