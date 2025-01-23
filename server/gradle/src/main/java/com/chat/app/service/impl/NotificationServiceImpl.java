package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Notification;
import com.chat.app.model.entity.extend.notification.FriendNotification;
import com.chat.app.model.entity.extend.notification.GroupNotification;
import com.chat.app.payload.response.NotificationResponse;
import com.chat.app.repository.jpa.NotificationRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {


    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private AccountService accountService;


    @Override
    public Notification getNotification(Long NotificationId) {
        return  notificationRepository.findById(NotificationId).orElse(null);
    }

    @Override
    public void notifyFriendRequestInvited(Long senderId, Long recipientId) throws ChatException {
        Account sender = accountService.getAccount(senderId);
        Account receiver = accountService.getAccount(recipientId);
        Notification notification = new FriendNotification(sender.getUsername() + " sent you a friend request",
                sender, receiver, new Date());
        notification = notificationRepository.save(notification);
        NotificationResponse notificationResponse = NotificationResponse.fromEntity((FriendNotification) notification);
        messagingTemplate.convertAndSend("/client/notification/friend" + recipientId, notificationResponse);
    }

    @Override
    public void notifyFriendRequestAccepted(Long senderId, Long recipientId) throws ChatException {
        Account sender = accountService.getAccount(senderId);
        Account recipient = accountService.getAccount(recipientId);
        Notification notification = new FriendNotification(sender.getUsername() + " accepted your friend request",
                sender, recipient, new Date());
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/client/notification" + recipientId, notification.getContent());
    }

    @Override
    public void notifyFriendRequestRejected(Long senderId, Long recipientId) throws ChatException {
        Account sender = accountService.getAccount(senderId);
        Account recipient = accountService.getAccount(recipientId);
        Notification notification = new FriendNotification(sender.getUsername() + "   rejected your friend request",
                sender, recipient, new Date());
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/client/notification" + recipientId, notification.getContent());
    }

    @Override
    public void markNotificationAsViewed(Long notificationId) {
        Notification notification = getNotification(notificationId);
        notification.setViewedDate(new Date());
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverAccountId(userId);
        List<NotificationResponse> notificationResponses = new ArrayList<>();
        for (Notification notification : notifications) {
            if (notification instanceof FriendNotification friendNotification) {
                notificationResponses.add(NotificationResponse.fromEntity(friendNotification));
            } else if (notification instanceof GroupNotification groupNotification) {
                notificationResponses.add(NotificationResponse.fromEntity(groupNotification));
            }
        }
        return notificationResponses;
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }


}
