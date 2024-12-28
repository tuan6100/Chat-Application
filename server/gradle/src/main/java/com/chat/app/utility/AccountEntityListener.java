package com.chat.app.utility;

import com.chat.app.model.elasticsearch.AccountDeleteEvent;
import com.chat.app.model.elasticsearch.AccountSyncEvent;
import com.chat.app.model.entity.Account;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AccountEntityListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostPersist
    @PostUpdate
    public void handleAccountChange(Account account) {
        eventPublisher.publishEvent(new AccountSyncEvent(account.getAccountId()));
    }

    @PostRemove
    public void handleAccountDelete(Account account) {
        eventPublisher.publishEvent(new AccountDeleteEvent(account.getAccountId()));
    }

}

