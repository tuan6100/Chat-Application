package com.chat.app.payload.response;


import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.MessageReaction;
import com.chat.app.payload.request.MessageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long messageId;
    private String randomId;
    private Long senderId;
    private String senderUsername;
    private String senderAvatar;
    private String content;
    private String type;
    private String sentTime;
    private Long replyToMessageId;
    private String replyToMessageContent;
    private List<Long> viewerIds;
    private List<String> viewerUsernames;
    private List<String> viewerAvatars;
    private List<MessageReactionResponse> reactions;
    private String status;


    public static MessageResponse fromEntity(Message message) {
        Account sender = message.getSender();
        List<MessageReaction> reactions = message.getReactions();
        List<MessageReactionResponse> reactionResponses = new ArrayList<>();
        reactions.forEach(reaction -> reactionResponses.add(MessageReactionResponse.fromEntity(reaction)));
        return new MessageResponse(
                message.getMessageId(),
                message.getRandomId(),
                sender.getAccountId(),
                sender.getUsername(),
                sender.getAvatar(),
                message.getContent(),
                message.getType().name(),
                message.getSentTime().toString(),
                message.getReplyTo() != null ? (message.getUnsent() ? null : message.getReplyTo().getMessageId()) : null,
                message.getReplyTo() != null ? (message.getUnsent() ? null : message.getReplyTo().getContent()) : null,
                message.getViewers().stream().map(Account::getAccountId).toList(),
                message.getViewers().stream().map(Account::getUsername).toList(),
                message.getViewers().stream().map(Account::getAvatar).toList(),
                reactionResponses,
                null
        );
    }

    public static MessageResponse fromRequest(MessageRequest request) {
        MessageResponse response = new MessageResponse();
        response.setSenderId(request.getSenderId());
        response.setContent(request.getContent());
        response.setType(request.getType());
        response.setSentTime(request.getSentTime().toString());
        response.setStatus(request.getStatus());
        return response;
    }
}
