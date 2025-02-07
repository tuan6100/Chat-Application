package com.chat.app.utility;

import com.chat.app.dto.CompositeKey;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CacheSyncManager {
    private final ConcurrentMap<Long, AtomicBoolean> cacheUpdateFlags = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, CountDownLatch> cacheLatches = new ConcurrentHashMap<>();


    public AtomicBoolean getOrCreateUpdateFlag(Long key) {
        return cacheUpdateFlags.computeIfAbsent(key, _ -> new AtomicBoolean(false));
    }

    public CountDownLatch getOrCreateLatch(Long key) {
        return cacheLatches.computeIfAbsent(key, _ -> new CountDownLatch(1));
    }

    public void resetLatch(Long key) {
        cacheLatches.put(key, new CountDownLatch(1));
    }
}
