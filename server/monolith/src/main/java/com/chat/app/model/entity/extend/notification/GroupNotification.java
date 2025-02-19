package com.chat.app.model.entity.extend.notification;


import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Notification;
import com.chat.app.model.entity.extend.chat.GroupChat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Getter
@Entity
@Table(name = "group_notification")
public class GroupNotification extends Notification {

    @ManyToOne
    @JoinColumn(name = "group_chat_id")
    private GroupChat group;


    public GroupNotification() {
        super();
    }

    public GroupNotification(String content, Account receiverAccount, GroupChat group, Date sentDate) {
        super(receiverAccount, content, sentDate);
        this.group = group;
    }

}
