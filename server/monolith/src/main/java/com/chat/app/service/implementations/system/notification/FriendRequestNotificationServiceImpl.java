package com.chat.app.service.implementations.system.notification;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Notification;
import com.chat.app.model.entity.extend.notification.FriendNotification;
import com.chat.app.payload.response.NotificationResponse;
import com.chat.app.repository.jpa.NotificationRepository;
import com.chat.app.service.interfaces.system.notification.FriendRequestNotificationService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FriendRequestNotificationServiceImpl implements FriendRequestNotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Override
    public void notifyFriendRequestInvited(Long senderId, Long receiverId) throws ChatException {
        Account sender = accountSearchService.searchAccountById(senderId);
        Account receiver = accountSearchService.searchAccountById(receiverId);
        Notification notification = new FriendNotification(sender.getUsername() + " sent you a friend request",
                sender, receiver, new Date());
        notification = notificationRepository.save(notification);
        NotificationResponse notificationResponse = NotificationResponse.fromEntity((FriendNotification) notification);
        messagingTemplate.convertAndSend("/client/notification/friend" + receiverId, notificationResponse);
    }

    @Override
    public void notifyFriendRequestAccepted(Long senderId, Long receiverId) throws ChatException {
        Account sender = accountSearchService.searchAccountById(senderId);
        Account receiver = accountSearchService.searchAccountById(receiverId);
        Notification notification = new FriendNotification(sender.getUsername() + " accepted your friend request",
                sender, receiver, new Date());
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/client/notification" + receiverId, notification.getContent());
    }
}
