package com.chat.app.repository.elasticsearch;

import com.chat.app.model.elasticsearch.AccountIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountElasticsearchRepository extends ElasticsearchRepository<AccountIndex, Long> {

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"username\"]}}")
    Page<AccountIndex> searchByUsername(String username, Pageable pageable);
}
