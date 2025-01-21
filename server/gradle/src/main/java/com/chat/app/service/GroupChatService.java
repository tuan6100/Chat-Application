package com.chat.app.service;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.GroupChat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
 public  interface GroupChatService extends ChatService {

     GroupChat getGroupChat(long chatId) throws ChatException;

     GroupChat createGroupChat(Account creator, GroupChatDTO groupChatDTO) ;

     GroupChat setPermission(Long groupChatId, Long accountId, GroupPermission permission) throws ChatException;

     GroupChat joinGroup(Long groupChatId, Long accountId, Long inviterId) throws ChatException;

     GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException;

     GroupChat addMember(Long groupChatId, Long userId, Long newMemberId) throws ChatException;

     GroupChat removeMember(Long groupChatId, Long userId, Long newMemberId)throws ChatException;

     GroupChat addAdmin(Long groupChatId, Long userId, Long newAdminId) throws ChatException;

     GroupChat removeAdmin(Long groupChatId, Long userId, Long adminId) throws ChatException;

     void deleteGroupChat(Long groupChatId, Long creatorId) throws Exception;

     List<Long> getGroupChatByMemberId(Long memberId);


}
