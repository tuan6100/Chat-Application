package com.chat.app.service;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.GroupChat;
import org.springframework.stereotype.Service;

@Service
public interface GroupChatService extends ChatService {

    public GroupChat getGroupChat(long chatId) throws ChatException;

    public GroupChat createGroupChat(Account creator, GroupChatDTO groupChatDTO) ;

    public GroupChat setPermission(Long groupChatId, Long accountId, GroupPermission permission) throws ChatException;

    public GroupChat joinGroup(Long groupChatId, Long accountId, Long inviterId) throws ChatException;

    public GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException;

    public GroupChat addMember(Long groupChatId, Long userId, Long newMemberId) throws ChatException;

    public GroupChat removeMember(Long groupChatId, Long userId, Long newMemberId)throws ChatException;

    public GroupChat addAdmin(Long groupChatId, Long userId, Long newAdminId) throws ChatException;

    public GroupChat removeAdmin(Long groupChatId, Long userId, Long adminId) throws ChatException;

    public void deleteGroupChat(Long groupChatId, Long creatorId) throws Exception;


}
