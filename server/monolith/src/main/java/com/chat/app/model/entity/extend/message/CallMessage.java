package com.chat.app.model.entity.extend.message;

import com.chat.app.enumeration.MessageType;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.dto.Offer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Getter
@Entity
@Table(name = "call_message")
public class CallMessage extends Message {

    @Column(name = "sdp", columnDefinition = "TEXT")
    private String sdp;

    @Column(name = "rtc_type")
    private String rtcType;


    public CallMessage(String randomId, Account sender, String content, MessageType type, Date date, Chat chat, Offer offer) {
        super(randomId, sender, content, type, date, chat);
        this.sdp = offer.getSdp();
        this.rtcType = offer.getType();
    }

    public CallMessage() {
    }
}
