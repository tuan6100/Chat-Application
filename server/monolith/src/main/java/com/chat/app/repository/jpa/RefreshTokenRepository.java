package com.chat.app.repository.jpa;

import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public
interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByAccount(Account account);

    void deleteByToken(String token);

}
