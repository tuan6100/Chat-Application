package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spam_chat")
public class SpamChat extends Chat {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spam_sender_account_id")
    public Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spam_receiver_account_id")
    public Account receiver;


    public SpamChat() {
        super();
    }

    public SpamChat(Theme theme, Account sender, Account receiver) {
        super(theme);
        this.sender = sender;
        this.receiver = receiver;
    }
}
