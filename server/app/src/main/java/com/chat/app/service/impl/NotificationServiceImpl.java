package com.chat.app.service.impl;

import com.chat.app.model.dto.NotificationDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.message.ImageMessage;
import com.chat.app.model.entity.extend.message.TextMessage;
import com.chat.app.payload.response.NotificationResponse;
import com.chat.app.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

    @Override
    public NotificationResponse notifyFriendRequestReceived(NotificationDTO notificationDTO) {
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
        String aboutTime = notificationDTO.getAboutTime();
        if (message instanceof TextMessage textMessage) {
            return new NotificationResponse(notificationDTO.getSenderAccount().getUsername(),
                                            ((TextMessage) message).getTextContent(),
                                            aboutTime);
        }
        if (message instanceof ImageMessage) {
            return new NotificationResponse("new message",
                                    notificationDTO.getSenderAccount().getUsername() + "just sent a photo",
                                            aboutTime);
        }
        return new NotificationResponse("new message",
                                notificationDTO.getSenderAccount().getUsername() + "just sent a file",
                                        aboutTime);
    }

    @Override
    public NotificationResponse notifyGroupInvitation(NotificationDTO notificationDTO, GroupChat groupChat) {
        String title = "New request";
        String message = "You are invited to the group" + groupChat.getChatName() + "by" + notificationDTO.getSenderAccount().getUsername();
        String aboutTime = notificationDTO.getAboutTime();
        return new NotificationResponse(title, message, aboutTime);
    }

    public static void sendNotification(Account account, NotificationResponse response) {

    }
}
