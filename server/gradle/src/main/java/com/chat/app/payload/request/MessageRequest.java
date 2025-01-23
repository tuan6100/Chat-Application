package com.chat.app.payload.request;


import lombok.Data;

import java.util.Date;

@Data
public class MessageRequest {

    protected String randomId;
    protected Long senderId;
    protected String content;
    protected String type;
    protected Date sentTime;
    protected String status;
    protected Long replyToMessageId;
    protected String replyToMessageContent;

}
