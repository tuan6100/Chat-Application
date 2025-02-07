package com.chat.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendStatusResponse {
    private Long friendId;
    private String status;
}
