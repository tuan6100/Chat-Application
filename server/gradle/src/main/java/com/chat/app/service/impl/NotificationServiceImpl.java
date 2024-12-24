package com.chat.app.service.impl;

import com.chat.app.model.dto.NotificationDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.payload.response.NotificationResponse;
import com.chat.app.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public NotificationResponse notifyFriendRequestInvited(NotificationDTO notificationDTO) {
        String message = String.format("You received a friend request from " + notificationDTO.getSenderAccount().getUsername());
        String aboutTime = notificationDTO.getAboutTime();
        return new NotificationResponse("New request", message, aboutTime);
    }

    @Override
    public NotificationResponse notifyFriendRequestAccepted(NotificationDTO notificationDTO) {
        String message = String.format("Your friend  to %s has been accepted.", notificationDTO.getSenderAccount().getUsername());
        String aboutTime = notificationDTO.getAboutTime();
        return new NotificationResponse("New request", message, aboutTime);
    }

    @Override
    public NotificationResponse notifyNewMessage(NotificationDTO notificationDTO, Message message) {
        return null;
    }

    @Override
    public NotificationResponse notifyGroupInvitation(NotificationDTO notificationDTO, GroupChat groupChat) {
        return null;
    }

    public static void sendNotification(Account account, NotificationResponse response) {

    }
}
