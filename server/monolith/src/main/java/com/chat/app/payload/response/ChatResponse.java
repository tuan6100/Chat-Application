package com.chat.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ChatResponse {

    private Long chatId;
    private String chatName;
    private String chatAvatar;
    private LatestMessage latestMessage;

    @Data
    @AllArgsConstructor
    public static class LatestMessage {
        private Long senderId;
        private String senderUsername;
        private String content;
        private String sentTime;
        private Boolean hasSeen;

        public static LatestMessage fromResponse(Long accountId, MessageResponse messageResponse) {
            return new LatestMessage(
                    messageResponse.getSenderId(),
                    messageResponse.getSenderUsername(),
                    messageResponse.getContent(),
                    messageResponse.getSentTime(),
                    messageResponse.getViewerIds().contains(accountId)
            );
        }
    }

}
