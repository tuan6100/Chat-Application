package com.chat.app.service.interfaces.user.information;

import com.chat.app.exception.ChatException;
import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.model.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountSearchService {

    Account searchAccountById(Long accountId) throws ChatException;

    Account searchAccountByEmail(String email);

    List<AccountIndex> searchAccountByUsername(String username);





}
