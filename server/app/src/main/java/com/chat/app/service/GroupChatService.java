package com.chat.app.service;

import com.chat.app.exception.AccountException;
import com.chat.app.model.dto.GroupChatDTO;
import com.chat.app.model.entity.Account;
import org.springframework.stereotype.Service;

@Service
public interface GroupChatService {

    public void createGroupChat(Account creator, GroupChatDTO groupChatDTO) ;

    public void setPermission(Long groupChatId, Long accountId, boolean permission);

    public void addMember(Long groupChatId, Long userId, String newMemberUsername) throws AccountException;

    public void removeMember(Long groupChatId, Long userId, String memberUsername)throws AccountException;

    public void addAdmin(Long groupChatId, Long userId, String newAdminUsername) throws AccountException;

    public void removeAdmin(Long groupChatId, Long userId, String adminUsername) throws AccountException;

    public void deleteGroupChat(Long groupChatId, Long accountId) throws Exception;


}
