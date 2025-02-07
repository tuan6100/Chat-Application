package com.chat.app.service.interfaces.system.notification;

import com.chat.app.model.entity.Notification;
import com.chat.app.payload.response.NotificationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    Notification getNotification(Long NotificationId);

    List<NotificationResponse> getNotificationsByAccountId(Long accountId);

    void markNotificationAsViewed(Long notificationId);

    void deleteNotification(Long notificationId);
}
