package com.chat.app.repository;

import com.chat.app.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    public Account findByEmail(String email);

    public Account findByUsername(String username);

    public List<Account> findByUsernameContaining(String username);
}
