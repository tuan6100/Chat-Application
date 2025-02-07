package com.chat.app.controller;


import com.chat.app.service.interfaces.system.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @PutMapping("/mark-as-read/{notificationId}")
    public void readNotification(@PathVariable Long notificationId) {
        notificationService.markNotificationAsViewed(notificationId);
    }

    @DeleteMapping("/delete/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

}
