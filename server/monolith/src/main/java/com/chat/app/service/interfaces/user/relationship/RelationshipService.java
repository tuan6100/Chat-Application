package com.chat.app.service.interfaces.user.relationship;

import com.chat.app.exception.ChatException;
import com.chat.app.payload.response.FriendStatusResponse;
import com.chat.app.model.entity.Relationship;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.payload.response.RelationshipResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
 public interface RelationshipService {

     Relationship getRelationship(Long relationshipId) throws ChatException;

     Long getRelationshipId(Long firstAccountId, Long secondAccountId);


     RelationshipResponse getRelationshipStatus(Long firstAccountId, Long secondAccountId) ;

     void inviteFriend(Long userId, Long friendId) throws ChatException;

     List<FriendStatusResponse> getInvitationsList(Long userId);

     void acceptFriend(Long userId, Long friendId) throws ChatException;

     List<AccountResponse> getFriendsList(Long userId);

     void rejectFriend(Long userId, Long friendId) throws ChatException;

     void unfriend(Long userId, Long friendId) throws ChatException;
    
     void blockUser(Long userId, Long friendId) throws ChatException;
    
     void unblockUser(Long userId, Long friendId) throws ChatException;
}
