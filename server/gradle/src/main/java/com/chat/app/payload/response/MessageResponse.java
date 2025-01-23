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

    protected Long messageId;
    protected String randomId;
    protected Long senderId;
    protected String senderUsername;
    protected String senderAvatar;
    protected String content;
    protected String type;
    protected String sentTime;
    protected Long replyToMessageId;
    protected String replyToMessageContent;
    protected List<Long> viewerIds;
    protected List<String> viewerUsernames;
    protected List<String> viewerAvatars;
    protected List<MessageReactionResponse> reactions;
    protected String status;


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
