package com.chat.app.payload.request;

import lombok.Data;

@Data
public class MessageRequest {
    private Long senderId;
    private String type;
    private String content;
}
