package com.chat.app.model.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "account")
public class AccountIndex {

    @Id
    private Long accountId;

    private String username;

    private String avatar;

}
