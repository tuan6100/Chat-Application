package com.chat.app.payload.request;

import lombok.Data;

@Data
public class MessageVerifierRequest {

    private String randomId;
    private String status;
}
