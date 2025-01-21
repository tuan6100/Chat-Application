package com.chat.app.payload.request;

import lombok.Data;

@Data
public class MessageVerifierRequest {

    private String randomId;
    private Long senderId;
    private String status;
}
