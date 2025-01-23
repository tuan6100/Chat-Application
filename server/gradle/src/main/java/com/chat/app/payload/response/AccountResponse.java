package com.chat.app.payload.response;

import com.chat.app.enumeration.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class AccountResponse {

    private Long accountId;
    private String username;
    private String email;
    private String avatar;
    private Boolean isOnline;
    private Date lastOnlineTime;
    private String relationshipStatus;


    public AccountResponse(Long accountId, String username, String avatar) {
        this.accountId = accountId;
        this.username = username;
        this.avatar = avatar;
    }

    public AccountResponse(Long accountId, String username, String email, String avatar) {
        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }

    public AccountResponse(Long accountId, String username, String avatar, Boolean isOnline, Date lastOnlineTime) {
        this.accountId = accountId;
        this.username = username;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.lastOnlineTime = lastOnlineTime;
    }

    public AccountResponse(Long accountId, String username, String avatar, Boolean isOnline, Date lastOnlineTime, String relationshipStatus) {
        this.accountId = accountId;
        this.username = username;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.lastOnlineTime = lastOnlineTime;
        this.relationshipStatus = relationshipStatus;
    }

}
