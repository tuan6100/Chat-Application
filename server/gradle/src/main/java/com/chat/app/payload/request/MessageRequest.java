package com.chat.app.payload.request;


import lombok.Data;

import java.util.Date;

@Data
public class MessageRequest {

    private String randomId;
    private Long senderId;
    private String content;
    private String type;
    private Date sentTime;
    private String status;
    private Long replyToMessageId;
    private String replyToMessageContent;

}
