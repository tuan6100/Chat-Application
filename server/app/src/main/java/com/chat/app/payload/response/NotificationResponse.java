package com.chat.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class NotificationResponse {
    private String title;
    private String message;
    private String aboutTime;
}
