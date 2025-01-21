package com.chat.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {

    private Long chatId;
    private String chatName;
    private String chatAvatar;

}
