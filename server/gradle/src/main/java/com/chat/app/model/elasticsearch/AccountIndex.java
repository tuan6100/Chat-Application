package com.chat.app.model.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "account")
public class AccountIndex {

    @Id
    private Long accountId;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String avatar;

}