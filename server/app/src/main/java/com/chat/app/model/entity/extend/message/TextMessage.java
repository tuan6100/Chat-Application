package com.chat.app.model.entity.extend.message;


import com.chat.app.model.entity.Message;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("TEXT")
public class TextMessage extends Message {

    @Lob
    @Column(name = "text_content")
    private String textContent;
}
