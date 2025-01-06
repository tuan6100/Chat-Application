package com.chat.app.payload.request;

import com.chat.app.enumeration.MessageType;
import com.chat.app.model.entity.Message;
import lombok.Data;

import java.util.Date;

@Data
public class MessageRequest {

    private Long senderId;
    private String content;
    private String type;
    private Date sentTime;
    private Long repliedMessageId;

}
