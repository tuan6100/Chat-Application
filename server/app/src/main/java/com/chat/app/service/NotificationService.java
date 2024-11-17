package com.chat.app.service;

import com.chat.app.model.dto.NotificationDTO;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.payload.response.NotificationResponse;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    public NotificationResponse notifyFriendRequestInvited(NotificationDTO notificationDTO);

    public NotificationResponse notifyFriendRequestAccepted(NotificationDTO notificationDTO);

    public NotificationResponse notifyNewMessage(NotificationDTO notificationDTO, Message message);

    public NotificationResponse notifyGroupInvitation(NotificationDTO notificationDTO, GroupChat groupChat);
}
