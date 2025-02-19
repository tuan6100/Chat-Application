package com.chat.app.service.implementations.user;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.CloudStorage;
import com.chat.app.model.redis.AccountOnlineStatus;
import com.chat.app.payload.request.AccountRequest;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.repository.elasticsearch.AccountSearchRepository;
import com.chat.app.repository.jpa.AccountRepository;
import com.chat.app.repository.jpa.CloudStorageRepository;
import com.chat.app.repository.redis.AccountOnlineStatusRepository;
import com.chat.app.service.interfaces.system.aws.S3Service;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.information.AccountService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService, AccountSearchService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountOnlineStatusRepository accountOnlineStatusRepository;

    @Autowired
    private AccountSearchRepository accountSearchRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private CloudStorageRepository personalMessageArchiveRepository;


    @Override
    public Account createAccount(Account account) {
        account = accountRepository.save(account);
        accountOnlineStatusRepository.save(new AccountOnlineStatus(account.getAccountId(), true, new Date()));
        accountSearchRepository.save(new AccountIndex(account.getAccountId(), account.getUsername(), account.getAvatar()));
        CloudStorage cloudStorage = new CloudStorage (Theme.SYSTEM, account, 0L);
        personalMessageArchiveRepository.save(cloudStorage);
        return account;
    }

    @Override
    public Account searchAccountById(Long accountId) throws ChatException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ChatException("Account not found"));
    }

    @Override
    public Account searchAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public List<AccountIndex> searchAccountByUsername(String username) {
        return accountSearchRepository.findByUsername(username);
    }

    @Override
    public AccountResponse getAccountResponse(Long accountId) throws ChatException {
        Account account = searchAccountById(accountId);
        AccountOnlineStatus accountStatus = accountOnlineStatusRepository.findByAccountId(accountId.toString());
        return new AccountResponse(accountId, account.getUsername(), account.getAvatar(),
                accountStatus.getIsOnline(), accountStatus.getLastOnlineTime());
    }

    @Override
    public void updateAccount(Long accountId, AccountRequest accountRequest) throws ChatException {
        Account account = searchAccountById(accountId);
        if (account == null) {
            throw new ChatException("Account not found");
        }
        if (accountRequest.getAvatar() != null && account.getAvatar() != null) {
            try {
                String defaultAvatar = "https://www.shutterstock.com/image-vector/vector-flat-illustration-grayscale-avatar-600nw-2281862025.jpg";
                if (!account.getAvatar().equals(defaultAvatar)) {
                    s3Service.deleteFile(account.getAvatar());
                }

            } catch (Exception e) {
                throw new ChatException("Failed to delete old avatar: " + e.getMessage());
            }
        }
        if (accountRequest.getAvatar() != null) {
            account.setAvatar(accountRequest.getAvatar());
        }
        if (accountRequest.getBirthdate() != null) {
            account.setBirthDate(accountRequest.getBirthdate());
        }
        if (accountRequest.getGender() != null) {
            account.setGender(accountRequest.getGender());
        }
        if (accountRequest.getBio() != null) {
            account.setBio(accountRequest.getBio());
        }
        accountRepository.save(account);
    }


    @Override
    public void deleteAccount(Long accountId) {
        accountSearchRepository.deleteById(accountId);
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
