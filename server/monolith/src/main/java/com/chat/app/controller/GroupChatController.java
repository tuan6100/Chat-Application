package com.chat.app.controller;


import com.chat.app.enumeration.GroupPermission;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.payload.request.NewGroupChatRequest;
import com.chat.app.service.interfaces.user.information.AccountService;
import com.chat.app.service.interfaces.chat.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/group/")
public class GroupChatController {

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private AccountService accountService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public GroupChat createGroupChat(@RequestBody NewGroupChatRequest request) throws ChatException {
        return groupChatService.createGroupChat(request);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/setPermission")
    public GroupChat setPermission(@RequestParam Long groupChatId, @RequestParam Long accountId, @RequestParam String permission) throws ChatException {
        return groupChatService.setPermission(groupChatId, accountId, GroupPermission.getPermission(permission));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{chatId}/join")
    public GroupChat joinGroup(@PathVariable Long chatId, @RequestParam Long accountId) throws ChatException {
        return groupChatService.joinGroup(chatId, accountId);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{chatId}/leave")
    public GroupChat leaveGroup(@PathVariable Long chatId, @RequestParam Long accountId) throws ChatException {
        return groupChatService.leaveGroup(chatId, accountId);
    }

}
