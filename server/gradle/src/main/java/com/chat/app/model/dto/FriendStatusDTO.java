package com.chat.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendStatusDTO {
    private Long friendId;
    private String status;
}
