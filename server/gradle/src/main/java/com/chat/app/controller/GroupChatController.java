package com.chat.app.controller;


import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.service.AccountService;
import com.chat.app.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/group/{chatId}")
public class GroupChatController {

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private AccountService accountService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public GroupChat createGroupChat(@RequestParam Long creatorId, @RequestBody GroupChatDTO groupChatDTO) throws ChatException {
        Account creator = accountService.getAccount(creatorId);
        return groupChatService.createGroupChat(creator, groupChatDTO);
    }

//    @PreAuthorize("isAuthenticated()")
//    @PutMapping("/setPermission")
//    public GroupChat setPermission(@RequestParam Long groupChatId, @RequestParam Long accountId, @RequestParam boolean permission) throws ChatException {
//        return groupChatService.setPermission(groupChatId, accountId, permission);
//    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/join/{adminId}")
    public GroupChat joinGroup(@PathVariable Long chatId, @RequestParam Long accountId, @PathVariable Long adminId) throws ChatException {
        return groupChatService.joinGroup(chatId, accountId, adminId);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/leave")
    public GroupChat leaveGroup(@PathVariable Long chatId, @RequestParam Long accountId) throws ChatException {
        return groupChatService.leaveGroup(chatId, accountId);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/addMember")
    public GroupChat addMember(@PathVariable Long chatId, @RequestParam Long userId, @RequestParam Long newMemberId) throws ChatException {
        return groupChatService.addMember(chatId, userId, newMemberId);
    }
}
