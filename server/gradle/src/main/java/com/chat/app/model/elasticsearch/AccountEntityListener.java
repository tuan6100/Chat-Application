package com.chat.app.model.elasticsearch;

import com.chat.app.model.entity.Account;
import com.chat.app.service.elasticsearch.AccountSyncService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountEntityListener {

    @Autowired
    private AccountSyncService accountSyncService;


    @PostPersist
    @PostUpdate
    public void handleAccountChange(Account account) {
        accountSyncService.syncAccountToElasticsearch(account.getAccountId());
    }

    @PostRemove
    public void handleAccountDelete(Account account) {
        accountSyncService.deleteAccountFromElasticsearch(account.getAccountId());
    }

}
