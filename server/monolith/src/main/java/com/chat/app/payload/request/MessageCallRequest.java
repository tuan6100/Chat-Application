package com.chat.app.payload.request;

import com.chat.app.dto.Offer;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class MessageCallRequest extends NewMessageRequest {

    private Offer offer;
}
