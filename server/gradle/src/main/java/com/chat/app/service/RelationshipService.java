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

     Relationship getRelationship(Long relationshipId) throws ChatException;

     Long getRelationshipId(Long firstAccountId, Long secondAccountId) throws ChatException;

     RelationshipResponse getRelationshipStatus(Long firstAccountId, Long secondAccountId) throws ChatException ;

     void inviteFriend(Long userId, Long friendId) throws ChatException;

     List<FriendStatusDTO> getInvitationsList(Long userId);

     void acceptFriend(Long userId, Long friendId) throws ChatException;

     List<AccountResponse> getFriendsList(Long userId);

     void rejectFriend(Long userId, Long friendId) throws ChatException;

     void unfriend(Long userId, Long friendId) throws ChatException;
    
     void blockFriend(Long userId, Long friendId) throws ChatException;
    
     void unblockFriend(Long userId, Long friendId) throws ChatException;
}
