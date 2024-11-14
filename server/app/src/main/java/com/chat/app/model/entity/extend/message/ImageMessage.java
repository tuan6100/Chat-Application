package com.chat.app.model.entity.extend.message;

import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("IMAGE")
public class ImageMessage extends Message {

    @Column(name = "image_url")
    private String imageUrl;

    public ImageMessage() {
        super();
    }

    public ImageMessage(Account sender, Date date, Chat chat, List<Message> replies, String imageUrl) {
        super(sender, date, chat, replies);
        this.imageUrl = imageUrl;
    }
}
