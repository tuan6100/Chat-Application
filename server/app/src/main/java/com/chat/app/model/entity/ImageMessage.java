package com.chat.app.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("IMAGE")
public class ImageMessage extends Message {

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
