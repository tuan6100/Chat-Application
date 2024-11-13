package com.chat.app.service;

import com.chat.app.model.dto.NotificationDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.ChatRoom;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chatroom.GroupChat;
import com.chat.app.payload.response.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    public NotificationResponse notifyFriendRequestReceived(NotificationDTO notificationDTO);

    public NotificationResponse notifyFriendRequestAccepted(NotificationDTO notificationDTO);

    public NotificationResponse notifyNewMessage(NotificationDTO notificationDTO, Message message);

    public NotificationResponse notifyGroupInvitation(NotificationDTO notificationDTO, GroupChat groupChat);
}
