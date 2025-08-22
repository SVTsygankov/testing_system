package com.svtsygankov.test_system.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginAttempt {
    private AtomicInteger countOfAttempts = new AtomicInteger(0);
    private Instant lastAttemptTime = Instant.now();

    public void incAttempts() {
        countOfAttempts.incrementAndGet();
        lastAttemptTime = Instant.now();
    }

    public boolean isBlocked(Integer second) {
        return countOfAttempts.get() >= 3 && Duration.between(lastAttemptTime, Instant.now()).getSeconds() <= second;
    }

    public void resetAttemptsAndUpdateTime() {
        countOfAttempts.set(0);
        lastAttemptTime = Instant.now();
    }
}
