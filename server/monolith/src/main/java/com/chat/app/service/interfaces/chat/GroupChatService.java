package com.chat.app.service.interfaces.chat;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.payload.request.NewGroupChatRequest;
import com.chat.app.payload.response.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
 public  interface GroupChatService extends ChatService {

     GroupChat getGroupChat(long chatId) throws ChatException;

     GroupChat createGroupChat(NewGroupChatRequest newGroupChatRequest) throws ChatException;

     GroupChat setPermission(Long groupChatId, Long accountId, GroupPermission permission) throws ChatException;

     GroupChat joinGroup(Long groupChatId, Long accountId) throws ChatException;

     GroupChat leaveGroup(Long groupChatId, Long accountId) throws ChatException;

     GroupChat addAdmin(Long groupChatId, Long userId, Long newAdminId) throws ChatException;

     GroupChat removeAdmin(Long groupChatId, Long userId, Long adminId) throws ChatException;

     void deleteGroupChat(Long groupChatId, Long creatorId) throws Exception;

     List<ChatResponse> getAllGroupChatsByMemberId(Long memberId) throws ChatException;


}
