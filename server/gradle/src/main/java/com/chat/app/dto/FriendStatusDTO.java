package com.chat.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendStatusDTO {
    private Long friendId;
    private String status;
}
