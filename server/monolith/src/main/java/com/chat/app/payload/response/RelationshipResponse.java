package com.chat.app.payload.response;

import com.chat.app.model.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Date;


@Data
@AllArgsConstructor
public class RelationshipResponse {

    private Long userId;
    private Long friendId;
    private String status;
    private Long chatId;

}
