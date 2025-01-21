package com.chat.app.payload.request;

import lombok.Data;

@Data
public class MessageUpdateRequest {

    private Long messageId;
    private Long accountId;
    private String content;
    private String reaction;

}
