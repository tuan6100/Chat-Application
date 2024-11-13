package com.chat.app.model.entity.extend.message;


import com.chat.app.model.entity.Message;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@DiscriminatorValue("FILE")
public class FileMessage extends Message {

    public static final int MAX_ALLOWED_SIZE = 512;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;


}
