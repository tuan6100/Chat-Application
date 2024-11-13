package com.chat.app.service;

import com.chat.app.exception.AccountException;
import com.chat.app.model.entity.Account;
import org.springframework.stereotype.Service;

@Service
public interface RelationshipService {
    
    public void inviteFriend(Long userId, Long friendId) throws AccountException;
    
    public void acceptFriend(Long userId, Long friendId);

    public void unfriend(Long userId, Long friendId);
    
    public void blockFriend(Long userId, Long friendId);
    
    public void unblockFriend(Long userId, Long friendId);
}
