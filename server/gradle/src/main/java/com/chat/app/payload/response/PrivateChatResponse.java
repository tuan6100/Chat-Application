package com.chat.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PrivateChatResponse {

    private Long chatId;
    private Long friendId;
    private LastestMessage lastestMessage;

    @Data
    @AllArgsConstructor
    public static class LastestMessage {
        private Long senderId;
        private String senderUsername;
        private String content;
        private Date sentTime;
        private Boolean hasSeen;
    }

}
