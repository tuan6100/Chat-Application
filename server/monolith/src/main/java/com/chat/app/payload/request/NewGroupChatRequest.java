package com.chat.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewGroupChatRequest {

    private String name;
    private String avatar;
    private Long creatorId;
    private List<Long> memberIds;
}
