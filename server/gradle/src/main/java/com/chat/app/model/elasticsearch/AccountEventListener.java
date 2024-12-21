package com.chat.app.model.elasticsearch;

import com.chat.app.service.elasticsearch.AccountSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccountEventListener {

    @Autowired
    private AccountSyncService accountSyncService;

    @TransactionalEventListener
    public void handleAccountSync(AccountSyncEvent event) {
        accountSyncService.syncAccountToElasticsearch(event.getAccountId());
    }

    @TransactionalEventListener
    public void handleAccountDelete(AccountDeleteEvent event) {
        accountSyncService.deleteAccountFromElasticsearch(event.getAccountId());
    }
}
