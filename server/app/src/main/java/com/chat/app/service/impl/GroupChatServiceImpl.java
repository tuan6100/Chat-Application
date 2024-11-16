package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.repository.AccountRepository;
import com.chat.app.repository.GroupChatRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

@Service
public class GroupChatServiceImpl extends ChatServiceImpl implements GroupChatService  {

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private AccountService accountService;


    @Override
    public GroupChat findGroupChatById(long chatId) throws ChatException {
        return (GroupChat) groupChatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("Group chat not found"));
    }

    @Override
    public GroupChat createGroupChat(Account creator, GroupChatDTO groupChatDTO) {
        GroupChat groupChat = new GroupChat();
        groupChat.setCreator(creator);
        groupChat.setChatName(groupChatDTO.getRoomName());
        groupChat.setAvatar(groupChatDTO.getAvatar());
        groupChat.setTheme(groupChatDTO.getTheme());
        groupChat.setMembers(groupChatDTO.getMembers());
        groupChat.setAdmins(new HashSet<>(Collections.singleton(creator)));
        groupChat.setCreatedAt(new Date());
        groupChat.setPermission(groupChatDTO.isPermission());
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat setPermission(Long groupChatId, Long accountId, boolean permission) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        if (accountId != groupChat.getCreator().getAccountId()) {
            throw new ChatException("You do not have permission to add a member to this group chat");
        }
        groupChat.setPermission(permission);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat joinGroup(Long groupChatId, Long accountId) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        Account newMember = accountService.findAccount(accountId);
        if (!groupChat.getPermission()) {
            groupChat.getMembers().add(newMember);
        }
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        Account newMember = accountService.findAccount(accountId);
        groupChat.getMembers().remove(newMember);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat addMember(Long groupChatId, Long adminId, String newMemberUsername) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        Account admin = accountService.findAccount(adminId);
        Account newMember = accountService.findAccount(newMemberUsername);
        if (groupChat.getMembers().contains(newMember)) {
            throw new ChatException("This user is already a member of this group chat");
        }
        if (groupChat.getPermission() && !groupChat.getAdmins().contains(admin)) {
            throw new ChatException("You do not have permission to add a member to this group chat");
        }
        groupChat.getMembers().add(newMember);
        return groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat removeMember(Long groupChatId, Long adminId, String memberUsername) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        Account admin = accountService.findAccount(adminId);
        Account member = accountService.findAccount(memberUsername);
        if (!groupChat.getMembers().contains(member)) {
            throw new ChatException("This user is not a member of this group chat");
        }
        if (groupChat.getPermission() && !groupChat.getAdmins().contains(admin) ) {
            throw new ChatException("You do not have permission to remove a member from this group chat");
        }
        groupChat.getMembers().remove(member);
        return  groupChatRepository.save(groupChat);
    }

    @Override
    public GroupChat addAdmin(Long groupChatId, Long userId, String newAdminUsername) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        Account newAdmin = accountService.findAccount(newAdminUsername);
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
    public GroupChat removeAdmin(Long groupChatId, Long userId, String adminUsername) throws ChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        Account admin = accountService.findAccount(adminUsername);
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
}
