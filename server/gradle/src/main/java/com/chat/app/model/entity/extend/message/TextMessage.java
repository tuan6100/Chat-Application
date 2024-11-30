package com.chat.app.model.entity.extend.message;


import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("TEXT")
public class TextMessage extends Message {

    @Lob
    @Column(name = "text_content")
    private String textContent;

    public TextMessage() {
        super();
    }

    public TextMessage(Account sender, Date date, Chat chat, List<Message> replies, String textContent) {
        super(sender, date, chat, replies);
        this.textContent = textContent;
    }
}
