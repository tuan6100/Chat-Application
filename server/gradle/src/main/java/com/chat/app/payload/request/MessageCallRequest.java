package com.chat.app.payload.request;

import com.chat.app.dto.Offer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageCallRequest extends MessageRequest{

    private Offer offer;
}
