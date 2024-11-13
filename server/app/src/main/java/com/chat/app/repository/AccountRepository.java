package com.chat.app.repository;

import com.chat.app.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    public  Account findByAccountId(Long accountId);
    public Account findByEmail(String email);
    public Account findByUsername(String username);
}
