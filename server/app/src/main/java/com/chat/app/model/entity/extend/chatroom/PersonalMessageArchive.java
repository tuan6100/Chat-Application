package com.chat.app.model.entity.extend.chatroom;

import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "personal_message_archive")
public class PersonalMessageArchive extends ChatRoom {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accountId")
    private Account account;

    public static final int MAX_STORAGE_SIZE = 1024;
}