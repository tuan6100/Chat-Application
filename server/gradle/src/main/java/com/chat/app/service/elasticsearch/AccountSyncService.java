package com.chat.app.service.elasticsearch;

import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.model.entity.Account;
import com.chat.app.repository.elasticsearch.AccountElasticsearchRepository;
import com.chat.app.repository.jpa.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AccountSyncService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountElasticsearchRepository accountElasticsearchRepository;


    @Async
    public void syncAccountToElasticsearch(Long accountId) {
        try {
            Account account = accountRepository.findById(accountId).orElseThrow();
            AccountIndex accountIndex = new AccountIndex();
            accountIndex.setAccountId(account.getAccountId());
            accountIndex.setUsername(account.getUsername());
            accountIndex.setAvatar(account.getAvatar());
            accountElasticsearchRepository.save(accountIndex);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    @Async
    public void deleteAccountFromElasticsearch(Long accountId) {
        accountElasticsearchRepository.deleteById(accountId);
    }


}
