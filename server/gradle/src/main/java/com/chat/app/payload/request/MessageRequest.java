package com.chat.app.payload.request;

import com.chat.app.enumeration.MessageType;
import com.chat.app.model.entity.Message;
import lombok.Data;

@Data
public class MessageRequest {

    private Long senderId;
    private String content;
    private MessageType type;
    private Long repliedMessageId;

}
