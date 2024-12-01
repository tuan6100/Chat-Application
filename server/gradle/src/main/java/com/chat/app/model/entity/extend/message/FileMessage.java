package com.chat.app.model.entity.extend.message;


import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("FILE")
public class FileMessage extends Message {

    public static final int MAX_ALLOWED_SIZE = 512;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    public FileMessage() {
        super();
    }

    public FileMessage(Account sender, Date date, Chat chat, List<Message> replies, String fileUrl, String fileName) {
        super(sender, date, chat, replies);
        this.fileUrl = fileUrl;
        this.fileName = fileName;
    }
}
