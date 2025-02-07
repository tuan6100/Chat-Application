package com.chat.app.service.interfaces.system.notification;

import com.chat.app.exception.ChatException;
import org.springframework.stereotype.Service;

@Service
public interface FriendRequestNotificationService {



    void notifyFriendRequestInvited(Long senderId, Long receiverId) throws ChatException;

    void notifyFriendRequestAccepted(Long senderId, Long receiverId) throws ChatException;






}
