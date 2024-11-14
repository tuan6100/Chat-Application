package com.chat.app.service;

import com.chat.app.exception.ChatException;
import org.springframework.stereotype.Service;

@Service
public interface RelationshipService {

    public void inviteFriend(Long userId, Long friendId) throws ChatException;
    
    public void acceptFriend(Long userId, Long friendId) throws ChatException;

    public void unfriend(Long userId, Long friendId) throws ChatException;
    
    public void blockFriend(Long userId, Long friendId) throws ChatException;
    
    public void unblockFriend(Long userId, Long friendId) throws ChatException;
}
