package com.chat.app.model.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountDeleteEvent {
    private Long accountId;
}