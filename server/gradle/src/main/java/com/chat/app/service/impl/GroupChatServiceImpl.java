package com.chat.app.service.impl;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.exception.ChatException;
import com.chat.app.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.repository.jpa.GroupChatRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.ChatService;
import com.chat.app.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class GroupChatServiceImpl extends ChatServiceImpl implements GroupChatService  {

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ChatService chatService;

    @Override
    public GroupChat getGroupChat(long chatId) throws ChatException {
        Chat chat =  chatService.getChat(chatId);
        if (!(chat instanceof GroupChat groupChat)) {
            throw new ChatException("This chat is not a group chat");
        }
        return groupChat;
    }

    @Override
    public GroupChat createGroupChat(Account creator, GroupChatDTO groupChatDTO) {
        GroupChat groupChat = new GroupChat();
        groupChat.setCreator(creator);
        groupChat.setGroupName(groupChatDTO.getRoomName());
        groupChat.setGroupAvatar(groupChatDTO.getAvatar());
        groupChat.setTheme(groupChatDTO.getTheme());
        groupChat.setMembers((HashSet<Account>) groupChatDTO.getMembers());
        groupChat.setAdmins(new HashSet<>(Collections.singleton(creator)));
        groupChat.setCreatedDate(new Date());
        groupChat.setPermission(groupChatDTO.getPermission());
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat setPermission(Long groupChatId, Long accountId, GroupPermission permission) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        if (accountId != groupChat.getCreator().getAccountId()) {
            throw new ChatException("You do not have permission to add a member to this group chat");
        }
        groupChat.setPermission(permission);
        return groupChatRepository.save(groupChat);
    }


    @Override
    public GroupChat joinGroup(Long groupChatId, Long accountId, Long inviterId) throws ChatException {
        GroupChat groupChat = getGroupChat(groupChatId);
//        Account newMember = accountService.getAccount(accountId);
//        Account
//        if (groupChat.getMembers().contains(newMember)) {
//            throw new ChatException("This user is already a member of this group chat");
//        }
//        if (groupChat.getPermission() == GroupPermission.PRIVATE) {
//            if (groupChat.getAdmins().contains(newMember)) {
//                throw new ChatException("You do not have permission to add a member to this group chat");
//            }
//            groupChat.getMembers().add(newMember);
//        }
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        Account newMember = accountService.getAccount(accountId);
        groupChat.getMembers().remove(newMember);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat addMember(Long groupChatId, Long adminId, Long newMemberId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
//        Account admin = accountService.getAccount(adminId);
//        Account newMember = accountService.getAccount(newMemberId);
//        if (groupChat.getMembers().contains(newMember)) {
//            throw new ChatException("This user is already a member of this group chat");
//        }
//        if (groupChat.getPermission() && !groupChat.getAdmins().contains(admin)) {
//            throw new ChatException("You do not have permission to add a member to this group chat");
//        }
//        groupChat.getMembers().add(newMember);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat removeMember(Long groupChatId, Long adminId, Long memberId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
//        Account admin = accountService.getAccount(adminId);
//        Account member = accountService.getAccount(memberId);
//        if (!groupChat.getMembers().contains(member)) {
//            throw new ChatException("This user is not a member of this group chat");
//        }
//        if (groupChat.getPermission() && !groupChat.getAdmins().contains(admin) ) {
//            throw new ChatException("You do not have permission to remove a member from this group chat");
//        }
//        groupChat.getMembers().remove(member);
        return  groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat addAdmin(Long groupChatId, Long userId, Long newAdminId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        Account newAdmin = accountService.getAccount(newAdminId);
        if (groupChat.getAdmins().contains(newAdmin)) {
            throw new ChatException("This user is already an admin of this group chat");
        }
        if (groupChat.getCreator().getAccountId() != userId) {
            throw new ChatException("You do not have permission to add an admin to this group chat");
        }
        groupChat.getAdmins().add(newAdmin);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat removeAdmin(Long groupChatId, Long userId, Long adminId) throws ChatException {
        GroupChat groupChat = this.getGroupChat(groupChatId);
        Account admin = accountService.getAccount(adminId);
        if (!groupChat.getAdmins().contains(admin)) {
            throw new ChatException("This user is not an admin of this group chat");
        }
        if (groupChat.getCreator().getAccountId() != userId) {
            throw new ChatException("You do not have permission to remove an admin from this group chat");
        }
        groupChat.getAdmins().remove(admin);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public void deleteGroupChat(Long groupChatId, Long creatorId) throws Exception {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        if (groupChat.getCreator().getAccountId() != creatorId) {
            throw new Exception("You do not have permission to delete this group chat");
        }
        groupChatRepository.delete(groupChat);
    }

    @Override
    public List<Long> getGroupChatByMemberId(Long memberId) {
        return groupChatRepository.findByMemberId(memberId);
    }
}
