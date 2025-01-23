package com.chat.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompositeKey {
    private Long accountId;
    private Long chatId;

    @Override
    public String toString() {
        return accountId + ":" + chatId;
    }
}