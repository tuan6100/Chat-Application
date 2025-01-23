package com.chat.app.payload.request;

import com.chat.app.dto.Offer;
import lombok.Data;

@Data
public class RTCSignalRequest {

    private String type;
    private Long chatId;
    private Offer offer;

}
