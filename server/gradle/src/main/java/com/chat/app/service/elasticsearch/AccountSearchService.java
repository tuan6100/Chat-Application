package com.chat.app.service.elasticsearch;

import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.repository.elasticsearch.AccountElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountSearchService {

    @Autowired
    private AccountElasticsearchRepository accountElasticsearchRepository;


    public List<AccountIndex> searchAccount(String username) {
        return accountElasticsearchRepository.findByUsername(username);
    }
}
