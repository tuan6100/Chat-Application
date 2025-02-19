package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;


@Getter
@Setter
@Entity
@Table(name = "cloud_storage")
public class CloudStorage extends Chat {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private Account account;

    @Column(name = "storage_used")
    @NumberFormat(style = NumberFormat.Style.PERCENT)
    private Long storageUsed;

    @Column(name = "max_storage_size", columnDefinition = "integer default 10240")
    public int MAX_STORAGE_SIZE = 1024 * 10 ;

    private static final String DEFAULT_AVATAR_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRHkXm8rnFhwBN9bx2zvm9d_G1sfQ31s9cy7d5ijMNs3x-kTyuz1pa4dQg0HJoklw3iy4o&usqp=CAU";




    public CloudStorage() {
        super();
    }

    public CloudStorage(Theme theme, Account account, Long storageUsed) {
        super(theme);
        this.account = account;
        this.storageUsed = storageUsed;
    }
}
