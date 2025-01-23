package com.chat.app.model.redis;


import com.chat.app.payload.response.MessageResponse;
import com.chat.app.dto.CompositeKey;
import com.chat.app.utility.StringJson;
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


    public MessageCache(CompositeKey key, List<MessageResponse> messageResponses) {
        this.messageCacheId = key.toString();
        this.setMessageResponses(messageResponses);
    }

    public List<MessageResponse> getMessageResponses() {
        StringJson<MessageResponse> stringJson = new StringJson<>(MessageResponse.class);
        return stringJson.fromJson(messageResponses);
    }

    public void setMessageResponses(List<MessageResponse> messageResponses) {
        StringJson<MessageResponse> stringJson = new StringJson<>(MessageResponse.class);
        this.messageResponses = stringJson.toJson(messageResponses);
    }


}
