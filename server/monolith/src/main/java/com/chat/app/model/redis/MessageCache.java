package com.chat.app.model.redis;


import com.chat.app.payload.response.MessageResponse;
import com.chat.app.utility.StringJsonUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("MessageCache")
public class MessageCache {

    @Id
    private String messageCacheId;

    private String messageResponses;


    public MessageCache(Long chatId, List<MessageResponse> messageResponses) {
        this.messageCacheId = chatId.toString();
        this.setMessageResponses(messageResponses);
    }


    public List<MessageResponse> getMessageResponses() {
        StringJsonUtil<MessageResponse> stringJsonUtil = new StringJsonUtil<>(MessageResponse.class);
        return stringJsonUtil.fromJson(this.messageResponses);
    }

    public void setMessageResponses(List<MessageResponse> messageResponses) {
        StringJsonUtil<MessageResponse> stringJsonUtil = new StringJsonUtil<>(MessageResponse.class);
        this.messageResponses = stringJsonUtil.toJson(messageResponses);
    }


}
