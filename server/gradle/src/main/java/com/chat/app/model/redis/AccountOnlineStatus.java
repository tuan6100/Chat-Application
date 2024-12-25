package com.chat.app.model.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash("AccountOnlineStatus")
@Data
public class AccountOnlineStatus {

    @Id
    private String accountId;

    private Boolean isOnline;

    private Date lastOnlineTime;


    public AccountOnlineStatus() {
    }

    public AccountOnlineStatus(Long accountId, Boolean isOnline, Date lastOnlineTime) {
        this.accountId = accountId.toString();
        this.isOnline = isOnline;
        this.lastOnlineTime = lastOnlineTime;
    }
}