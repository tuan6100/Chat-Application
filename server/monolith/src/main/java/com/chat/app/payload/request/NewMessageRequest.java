package com.chat.app.payload.request;

import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMessageRequest extends MessageRequest {

    protected String randomId;
    protected String content;
    protected String type;
    protected Date sentTime;
    protected String status;
    protected Long replyToMessageId;
    protected String replyToMessageContent;
}
