package com.chat.app.service.impl;

import com.chat.app.exception.AccountException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chatroom.GroupChat;
import com.chat.app.repository.AccountRepository;
import com.chat.app.repository.GroupChatRepository;
import com.chat.app.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GroupChatServiceImpl implements GroupChatService {


    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void createGroupChat(Account creator, GroupChatDTO groupChatDTO) {
        GroupChat groupChat = new GroupChat();
        groupChat.setCreator(creator);
        groupChat.setRoomName(groupChatDTO.getRoomName());
        groupChat.setAvatar(groupChatDTO.getAvatar());
        groupChat.setTheme(groupChatDTO.getTheme());
        groupChat.setMembers(groupChatDTO.getMembers());
        groupChat.setAdmins(groupChatDTO.getAdmins());
        groupChat.setCreatedAt(new Date());
        groupChat.setPermission(groupChatDTO.isPermission());
        groupChatRepository.save(groupChat);
    }

    @Override
    public void setPermission(Long groupChatId, Long accountId, boolean permission) {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        if (groupChat.getCreator().getAccountId() == accountId) {
            groupChat.setPermission(permission);
            groupChatRepository.save(groupChat);
        }
    }

    @Override
    public void addMember(Long groupChatId, Long accountId, String newMemberUsername) throws AccountException {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        Account newMember = accountRepository.findByUsername(newMemberUsername);
        if (groupChat.getMembers().contains(newMember)) {
            throw new AccountException("This user is already a member of this group chat");
        }
        if (groupChat.getPermission()) {
            if (groupChat.getCreator().getAccountId() != accountId) {
                throw new AccountException("You do not have permission to add a member to this group chat");
            }
        }
        groupChat.getMembers().add(newMember);
        groupChatRepository.save(groupChat);
    }

    @Override
    public void removeMember(Long groupChatId, Long userId, String memberUsername) throws AccountException {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        Account member = accountRepository.findByUsername(memberUsername);
        if (!groupChat.getMembers().contains(member)) {
            throw new AccountException("This user is not a member of this group chat");
        }
        if (groupChat.getCreator().getAccountId() != userId) {
            throw new AccountException("You do not have permission to remove a member from this group chat");
        }
        groupChat.getMembers().remove(member);
        groupChatRepository.save(groupChat);
    }

    @Override
    public void addAdmin(Long groupChatId, Long userId, String newAdminUsername) throws AccountException {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        Account newAdmin = accountRepository.findByUsername(newAdminUsername);
        if (groupChat.getAdmins().contains(newAdmin)) {
            throw new AccountException("This user is already an admin of this group chat");
        }
        if (groupChat.getCreator().getAccountId() != userId) {
            throw new AccountException("You do not have permission to add an admin to this group chat");
        }
        groupChat.getAdmins().add(newAdmin);
        groupChatRepository.save(groupChat);
    }

    @Override
    public void removeAdmin(Long groupChatId, Long userId, String adminUsername) throws AccountException  {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        Account admin = accountRepository.findByUsername(adminUsername);
        if (!groupChat.getAdmins().contains(admin)) {
            throw new AccountException("This user is not an admin of this group chat");
        }
        if (groupChat.getCreator().getAccountId() != userId) {
            throw new AccountException("You do not have permission to remove an admin from this group chat");
        }
        groupChat.getAdmins().remove(admin);
    }

    @Override
    public void deleteGroupChat(Long groupChatId, Long accountId) throws Exception {
        GroupChat groupChat = (GroupChat) groupChatRepository.findById(groupChatId).get();
        if (groupChat.getCreator().getAccountId() != accountId) {
            throw new Exception("You do not have permission to delete this group chat");
        }
        groupChatRepository.delete(groupChat);
    }
}
