package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Notification;
import com.chat.app.payload.response.NotificationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    Notification getNotification(Long NotificationId);

    void notifyFriendRequestInvited(Long senderId, Long recipientId) throws ChatException;

    void notifyFriendRequestAccepted(Long senderId, Long recipientId) throws ChatException;

    void notifyFriendRequestRejected(Long senderId, Long recipientId) throws ChatException;

    void markNotificationAsViewed(Long notificationId);

    List<NotificationResponse> getUserNotifications(Long userId);

    void deleteNotification(Long notificationId);
}
