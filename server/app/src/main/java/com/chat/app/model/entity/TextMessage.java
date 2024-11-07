package com.chat.app.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("TEXT")
public class TextMessage extends Message {

    @Column(name = "text_content", nullable = false)
    private String textContent;
}
