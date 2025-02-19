package com.chat.app.model.entity.extend.notification;

import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Notification;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Getter
@Entity
@Table(name = "friend_notification")
public class FriendNotification extends Notification {

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account senderAccount;


    public FriendNotification() {
        super();
    }

    public FriendNotification(String content, Account senderAccount, Account receiverAccount, Date sentDate) {
        super(receiverAccount, content, sentDate);
        this.senderAccount = senderAccount;
    }
}
