package com.chat.app.service.implementations.chat;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.SpamChat;
import com.chat.app.payload.request.NewGroupChatRequest;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.GroupChatRepository;
import com.chat.app.repository.jpa.SpamChatRepository;
import com.chat.app.service.interfaces.chat.SpamChatService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.information.AccountService;
import com.chat.app.service.interfaces.chat.ChatService;
import com.chat.app.service.interfaces.chat.GroupChatService;
import com.chat.app.service.interfaces.user.relationship.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GroupChatServiceImpl extends ChatServiceImpl implements GroupChatService  {

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    @Qualifier("chatServiceImpl")
    private ChatService chatService;


    @Override
    public GroupChat getGroupChat(long chatId) throws ChatException {
        return groupChatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("Group chat not found"));
    }

    @Override
    public GroupChat createGroupChat(NewGroupChatRequest newGroupChatRequest) throws ChatException {
        GroupChat groupChat = new GroupChat();
        groupChat.setCreator(accountSearchService.searchAccountById(newGroupChatRequest.getCreatorId()));
        groupChat.setGroupName(newGroupChatRequest.getName());
        groupChat.setGroupAvatar(newGroupChatRequest.getAvatar());
        List<Account> members = new ArrayList<>();
        for (Long memberId : newGroupChatRequest.getMemberIds()) {
            members.add(accountSearchService.searchAccountById(memberId));
        }
        groupChat.setMembers(members);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat setPermission(Long groupChatId, Long accountId, GroupPermission permission) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        if (!Objects.equals(accountId, groupChat.getCreator().getAccountId())) {
            throw new ChatException("You do not have permission to add a member to this group chat");
        }
        groupChat.setPermission(permission);
        return groupChatRepository.save(groupChat);
    }


    @Override
    public GroupChat joinGroup(Long groupChatId, Long accountId) throws ChatException {
        GroupChat groupChat = getGroupChat(groupChatId);
        Account newMember = accountSearchService.searchAccountById(accountId);
        if (groupChat.getMembers().contains(newMember)) {
            throw new ChatException("This user is already a member of this group chat");
        }
        groupChat.getMembers().add(newMember);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        Account newMember = accountSearchService.searchAccountById(accountId);
        groupChat.getMembers().remove(newMember);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat addAdmin(Long groupChatId, Long userId, Long newAdminId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        Account newAdmin = accountSearchService.searchAccountById(newAdminId);
        if (groupChat.getAdmins().contains(newAdmin)) {
            throw new ChatException("This user is already an admin of this group chat");
        }
        if (!Objects.equals(groupChat.getCreator().getAccountId(), userId)) {
            throw new ChatException("You do not have permission to add an admin to this group chat");
        }
        groupChat.getAdmins().add(newAdmin);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat removeAdmin(Long groupChatId, Long userId, Long adminId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        Account admin = accountSearchService.searchAccountById(adminId);
        if (!groupChat.getAdmins().contains(admin)) {
            throw new ChatException("This user is not an admin of this group chat");
        }
        if (!Objects.equals(groupChat.getCreator().getAccountId(), userId)) {
            throw new ChatException("You do not have permission to remove an admin from this group chat");
        }
        groupChat.getAdmins().remove(admin);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public void deleteGroupChat(Long groupChatId, Long creatorId) throws Exception {
        GroupChat groupChat = getGroupChat(groupChatId);
        if (!Objects.equals(groupChat.getCreator().getAccountId(), creatorId)) {
            throw new Exception("You do not have permission to delete this group chat");
        }
        groupChatRepository.delete(groupChat);
    }

    @Override
    public List<ChatResponse> getAllGroupChatsByMemberId(Long memberId) throws ChatException {
        Account member = accountSearchService.searchAccountById(memberId);
        List<Long> groupChatIds = groupChatRepository.findByMemberId(memberId);
        return groupChatIds.stream().parallel()
                .map(chatId -> new ChatResponse(chatId, member.getUsername(), member.getAvatar(),
                        ChatResponse.LatestMessage.fromResponse(memberId, getLastestMessage(chatId))))
                .toList();
    }

}
