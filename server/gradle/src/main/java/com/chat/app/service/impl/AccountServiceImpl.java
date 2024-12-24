package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.repository.jpa.AccountRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.RelationshipService;
import com.chat.app.service.aws.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private RelationshipService relationshipService;

    private final S3Service s3Service;
    @Autowired
    public AccountServiceImpl(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
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


}
