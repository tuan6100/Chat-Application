package com.chat.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageUpdateRequest extends MessageRequest {

    private String content;
    private String reaction;
}
