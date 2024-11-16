package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.GroupChat;
import org.springframework.stereotype.Service;

@Service
public interface GroupChatService extends ChatService {

    public GroupChat findGroupChatById(long chatId) throws ChatException;

    public GroupChat createGroupChat(Account creator, GroupChatDTO groupChatDTO) ;

    public GroupChat setPermission(Long groupChatId, Long accountId, boolean permission) throws ChatException;

    public GroupChat joinGroup(Long groupChatId, Long accountId) throws ChatException;

    public GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException;

    public GroupChat addMember(Long groupChatId, Long userId, String newMemberUsername) throws ChatException;

    public GroupChat removeMember(Long groupChatId, Long userId, String memberUsername)throws ChatException;

    public GroupChat addAdmin(Long groupChatId, Long userId, String newAdminUsername) throws ChatException;

    public GroupChat removeAdmin(Long groupChatId, Long userId, String adminUsername) throws ChatException;

    public void deleteGroupChat(Long groupChatId, Long creatorId) throws Exception;


}
