package com.chat.app.repository.redis;

import com.chat.app.model.redis.AccountOnlineStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountOnlineStatusRepository extends CrudRepository<AccountOnlineStatus, Long> {
    AccountOnlineStatus findByAccountId(String accountId);
    void deleteByAccountId(String accountId);
}
