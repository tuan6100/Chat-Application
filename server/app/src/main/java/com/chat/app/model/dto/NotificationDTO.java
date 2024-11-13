package com.chat.app.model.dto;

import com.chat.app.model.entity.Account;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
public class NotificationDTO {
    private Account senderAccount;
    private Account recipientAccount;
    private Date sentDate;
    private Date viewedDate;

    public String getAboutTime() {
        long timeDiff = viewedDate.getTime() - sentDate.getTime();
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
