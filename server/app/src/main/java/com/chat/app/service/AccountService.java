package com.chat.app.service;

import com.chat.app.exception.AccountException;
import com.chat.app.model.dto.AccountDto;
import com.chat.app.model.entity.Account;
import com.chat.app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    public Account findAccountProfile(String jwt);
    public Account createAccount(Account account) throws AccountException;
    public Account updateAccount(Long accountId, AccountDto accountDto) throws AccountException;
    public void deleteAccount(Long accountId);
    public List<Account> searchAccount(String query);

}
