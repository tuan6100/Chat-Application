package com.chat.app.repository.elasticsearch;

import com.chat.app.model.elasticsearch.MessageIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageSearchRepository extends ElasticsearchRepository<MessageIndex, Long> {

    @Query("""
           {
                "bool": {
                    "must": [
                        { "wildcard": { "content": "*?0*" } },
                        { "term": { "chatId": "?1" } }
                    ]
                }
           }
    """)
    List<MessageIndex> findByContent(String content, Long chatId);
}
