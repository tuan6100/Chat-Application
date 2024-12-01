package com.chat.app.payload.response;

import com.chat.app.enumeration.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private String username;
    private String avatar;
    private List<FriendResponse> friends;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendResponse {
        private Long friendId;
        private String username;
        private String avatar;
        private UserStatus status;
    }
}
