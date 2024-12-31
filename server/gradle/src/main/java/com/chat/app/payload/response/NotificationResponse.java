package com.chat.app.payload.response;


import com.chat.app.model.entity.extend.notification.FriendNotification;
import com.chat.app.model.entity.extend.notification.GroupNotification;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Data
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String type;
    private String name;
    private String avatar;
    private String content;
    private String aboutTime;


    public static NotificationResponse fromEntity(FriendNotification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getSenderAccount().getAccountId());
        response.setType("friend_request");
        response.setName(notification.getSenderAccount().getUsername());
        response.setAvatar(notification.getSenderAccount().getAvatar());
        response.setContent(notification.getContent());
        response.setAboutTime(response.getAboutTime(notification.getSentDate()));
        return response;
    }

    public static NotificationResponse fromEntity(GroupNotification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getGroup().getChatId());
        response.setType("group_invite");
        response.setName(notification.getGroup().getGroupName());
        response.setAvatar(notification.getGroup().getGroupAvatar());
        response.setContent(notification.getContent());
        response.setAboutTime(response.getAboutTime(notification.getSentDate()));
        return response;
    }

    private String getAboutTime(Date sentDate) {
        Date currentDate = new Date();
        long timeDiff = currentDate.getTime() - sentDate.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
        long days = TimeUnit.MILLISECONDS.toDays(timeDiff);
        if (days == 0) {
            if (minutes < 1) {
                return seconds == 1 ? "one second ago" : seconds + " seconds ago";
            } else if (minutes < 60) {
                return minutes == 1 ? "one minute ago" : minutes + " minutes ago";
            } else if (hours < 24) {
                return hours == 1 ? "one hour ago" : hours + " hours ago";
            }
        }
        if (days == 1) {
            return "yesterday at " + new SimpleDateFormat("HH:mm").format(sentDate);
        }
        DayOfWeek dayOfWeek = DayOfWeek.of(sentDate.getDay());
        if (days <= 7) {
            return  dayOfWeek + " at " + new SimpleDateFormat("HH:mm").format(sentDate);
        }
        return new SimpleDateFormat("MMM dd, yyyy").format(sentDate);
    }

}
