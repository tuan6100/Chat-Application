package com.chat.app.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockManagerUtil {
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();


    public ReentrantLock getLock(Long chatId) {
        return lockMap.computeIfAbsent(chatId, k -> new ReentrantLock());
    }
}
