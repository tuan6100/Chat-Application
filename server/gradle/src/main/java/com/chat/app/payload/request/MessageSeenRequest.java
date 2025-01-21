package com.chat.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageSeenRequest {

    private Long messageId;
    private Long viewerId;

}
