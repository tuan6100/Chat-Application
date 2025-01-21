package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.model.redis.AccountOnlineStatus;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.payload.response.PrivateChatResponse;
import com.chat.app.repository.jpa.AccountRepository;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.redis.AccountOnlineStatusRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.aws.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountOnlineStatusRepository accountOnlineStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private PrivateChatServiceImpl privateChatService;

    @Autowired
    @Lazy
    private GroupChatServiceImpl groupChatService;



    @Override
    public Account createAccount(Account account) {
        accountRepository.save(account);
        accountOnlineStatusRepository.save(new AccountOnlineStatus(account.getAccountId(), true, new Date()));
        return account;
    }

    @Override
    public Account getAccount(Long accountId) throws ChatException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ChatException("Account not found"));
    }

    @Override
    public Account getAccount(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public AccountResponse getAccountResponse(Long accountId) throws ChatException {
        Account account = getAccount(accountId);
        AccountOnlineStatus accountStatus = accountOnlineStatusRepository.findByAccountId(accountId.toString());
        return new AccountResponse(accountId, account.getUsername(), account.getAvatar(),
                accountStatus.getIsOnline(), accountStatus.getLastOnlineTime());
    }

    @Override
    public void updateAccount(Long accountId, AccountDTO accountDto) throws ChatException {
        Account account = getAccount(accountId);
        if (account == null) {
            throw new ChatException("Account not found");
        }
        if (accountDto.getAvatar() != null && account.getAvatar() != null) {
            try {
                String defaultAvatar = "https://www.shutterstock.com/image-vector/vector-flat-illustration-grayscale-avatar-600nw-2281862025.jpg";
                if (!account.getAvatar().equals(defaultAvatar)) {
                    s3Service.deleteFile(account.getAvatar());
                }

            } catch (Exception e) {
                throw new ChatException("Failed to delete old avatar: " + e.getMessage());
            }
        }
        if (accountDto.getAvatar() != null) {
            account.setAvatar(accountDto.getAvatar());
        }
        if (accountDto.getBirthdate() != null) {
            account.setBirthDate(accountDto.getBirthdate());
        }
        if (accountDto.getGender() != null) {
            account.setGender(accountDto.getGender());
        }
        if (accountDto.getBio() != null) {
            account.setBio(accountDto.getBio());
        }
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Override
    public void markUserOnline(Long accountId) {
        AccountOnlineStatus accountStatus = accountOnlineStatusRepository.findByAccountId(accountId.toString());
        if (accountStatus == null) {
            accountStatus = new AccountOnlineStatus();
            accountStatus.setAccountId(accountId.toString());
        }
        accountStatus.setIsOnline(true);
        accountStatus.setLastOnlineTime(new Date());
        accountOnlineStatusRepository.save(accountStatus);
    }

    @Override
    public void markUserOffline(Long accountId) {
        AccountOnlineStatus accountStatus = accountOnlineStatusRepository.findByAccountId(accountId.toString());
        if (accountStatus != null) {
            accountStatus.setIsOnline(false);
            accountStatus.setLastOnlineTime(new Date());
            accountOnlineStatusRepository.save(accountStatus);
        }
    }

    @Override
    public boolean isUserOnline(Long accountId) {
        AccountOnlineStatus accountStatus = accountOnlineStatusRepository.findByAccountId(accountId.toString());
        return accountStatus != null && Boolean.TRUE.equals(accountStatus.getIsOnline());
    }

    @Override
    public Date getLastOnlineTime(Long accountId) {
        AccountOnlineStatus accountStatus = accountOnlineStatusRepository.findByAccountId(accountId.toString());
        return (accountStatus != null) ? accountStatus.getLastOnlineTime() : null;
    }

    @Override
    public List<PrivateChatResponse> getAllPrivateChat(Long accountId) {
        List<Long>  privateChatIds = privateChatService.findAllPrivateChatsByAccountId(accountId);
        List<PrivateChatResponse> responses = new ArrayList<>();
        for (Long chatId : privateChatIds) {
            PrivateChat privateChat = privateChatService.getChat(chatId);
            Long friendId = (Objects.equals(privateChat.getRelationship().getFirstAccount().getAccountId(), accountId)) ?
                    privateChat.getRelationship().getSecondAccount().getAccountId() : privateChat.getRelationship().getFirstAccount().getAccountId();
            Message lastestMessage = privateChatService.getLastestMessage(chatId);
            if (lastestMessage.getUnsent() != null && lastestMessage.getUnsent()) {
                lastestMessage.setContent("This message has been deleted");
            }
            Boolean hasSeen = lastestMessage.getViewers().stream().anyMatch(viewer -> viewer.getAccountId().equals(accountId)) || lastestMessage.getSender().getAccountId().equals(accountId);
            responses.add(new PrivateChatResponse(chatId, friendId,
                    new PrivateChatResponse.LastestMessage(lastestMessage.getSender().getAccountId(), lastestMessage.getSender().getUsername(), lastestMessage.getContent(), lastestMessage.getSentTime(), hasSeen)));
        }
        return responses;
    }

    @Override
    public List<ChatResponse> getAllChatsByAccountId(Long accountId) {
        List<Long>  privateChatIds = privateChatService.findAllPrivateChatsByAccountId(accountId);
        List<ChatResponse> privateChatResponses = new ArrayList<>();
        privateChatIds.forEach((chatId) -> {
            PrivateChat privateChat = privateChatService.getChat(chatId);
            Long friendId = (Objects.equals(privateChat.getRelationship().getFirstAccount().getAccountId(), accountId)) ?
                    privateChat.getRelationship().getSecondAccount().getAccountId() : privateChat.getRelationship().getFirstAccount().getAccountId();
            try {
                String chatName = getAccount(friendId).getUsername();
                String chatAvatar = getAccount(friendId).getAvatar();
                privateChatResponses.add(new ChatResponse(chatId, chatName, chatAvatar));
            } catch (ChatException e) {
                System.out.println(e.getMessage());
            }
        });
        List<Long> groupChatIds = groupChatService.getGroupChatByMemberId(accountId);
        List<ChatResponse> groupChatResponses = new ArrayList<>();
        groupChatIds.forEach((chatId) -> {
            GroupChat groupChat = (GroupChat) groupChatService.getChat(chatId);
            groupChatResponses.add(new ChatResponse(chatId, groupChat.getGroupName(), groupChat.getGroupAvatar()));
        });
        return Stream.concat(privateChatResponses.stream(), groupChatResponses.stream()).toList();
    }


}
