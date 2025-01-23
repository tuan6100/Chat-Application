package com.chat.app.payload.response;

import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.MessageReaction;
import com.chat.app.model.entity.extend.message.CallMessage;
import com.chat.app.dto.Offer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageCallResponse extends MessageResponse {

    private Long chatId;
    private Offer offer;

    public MessageCallResponse(Long messageId, String randomId, Long accountId, String username, String avatar, String content, String name, String string, Long aLong, String s, List<Long> list, List<String> list1, List<String> list2, List<MessageReactionResponse> o, String o1, Long chatId, Offer offer) {
        super(messageId, randomId, accountId, username, avatar, content, name, string, aLong, s, list, list1, list2, o, o1);
        this.chatId = chatId;
        this.offer = offer;
    }


    public static MessageCallResponse fromEntity(CallMessage message) {
        List<MessageReaction> reactions = message.getReactions();
        List<MessageReactionResponse> reactionResponses = new ArrayList<>();
        reactions.forEach(reaction -> reactionResponses.add(MessageReactionResponse.fromEntity(reaction)));
        return new MessageCallResponse(
                message.getMessageId(),
                message.getRandomId(),
                message.getSender().getAccountId(),
                message.getSender().getUsername(),
                message.getSender().getAvatar(),
                message.getContent(),
                message.getType().name(),
                message.getSentTime().toString(),
                message.getReplyTo() != null ? (message.getUnsent() ? null : message.getReplyTo().getMessageId()) : null,
                message.getReplyTo() != null ? (message.getUnsent() ? null : message.getReplyTo().getContent()) : null,
                message.getViewers().stream().map(Account::getAccountId).toList(),
                message.getViewers().stream().map(Account::getUsername).toList(),
                message.getViewers().stream().map(Account::getAvatar).toList(),
                reactionResponses,
                null,
                message.getChat().getChatId(),
                new Offer(message.getSdp(), message.getRtcType())
        );

    }
}
