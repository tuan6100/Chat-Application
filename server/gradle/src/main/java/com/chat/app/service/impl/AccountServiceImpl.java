package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.redis.AccountOnlineStatus;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.repository.jpa.AccountRepository;
import com.chat.app.repository.redis.AccountOnlineStatusRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.aws.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

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


    @Override
    public Account createAccount(Account account) {
        accountRepository.save(account);
        accountOnlineStatusRepository.save(new AccountOnlineStatus(account.getAccountId(), false, new Date()));
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


}
