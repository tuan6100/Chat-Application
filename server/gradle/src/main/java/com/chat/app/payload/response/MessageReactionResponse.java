package com.chat.app.payload.response;

import com.chat.app.model.entity.MessageReaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReactionResponse {

    private Long accountId;
    private String username;
    private String avatar;
    private String reaction;

    public static MessageReactionResponse fromEntity(MessageReaction reaction) {
        return new MessageReactionResponse(
                reaction.getAccount().getAccountId(),
                reaction.getAccount().getUsername(),
                reaction.getAccount().getAvatar(),
                reaction.getReaction()
        );
    }

}
