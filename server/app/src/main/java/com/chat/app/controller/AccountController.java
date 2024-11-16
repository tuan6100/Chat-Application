package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;



    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) throws ChatException {
        return accountService.findAccount(id);
    }


}
