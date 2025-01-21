package com.chat.app.repository.elasticsearch;

import com.chat.app.model.elasticsearch.AccountIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountSearchRepository extends ElasticsearchRepository<AccountIndex, Long> {


    @Query("{\"wildcard\": {\"username\": \"*?0*\"}}")
    List<AccountIndex> findByUsername(String username);

}
