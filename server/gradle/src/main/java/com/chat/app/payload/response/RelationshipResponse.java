package com.chat.app.payload.response;

import com.chat.app.model.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Date;


@Data
@AllArgsConstructor
public class RelationshipResponse {

    private Long accountId;
    private String username;
    private String email;
    private String avatar;
    private String status;
    private Boolean isFriend;
//    private Boolean isOnline;
//    private Date lastOnlineTime;
    private Page<Message> messages;

}
