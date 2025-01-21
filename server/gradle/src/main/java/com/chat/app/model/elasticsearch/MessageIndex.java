package com.chat.app.model.elasticsearch;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@Document(indexName = "message")
public class MessageIndex {

    @Id
    private Long messageId;

    @Field(type = FieldType.Text)
    private String content;

}
