package com.svtsygankov.test_system.service;


import com.svtsygankov.test_system.entity.LoginAttempt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAttemptServiceImpl implements LoginAttemptService{

    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    @Override
    public boolean isBlocked(String login) {
        var attempt = loginAttempts.computeIfAbsent(login, k -> new LoginAttempt());
        return attempt.isBlocked(120);
    }

    @Override
    public void recordFailedAttempt(String login) {
        var attempt = loginAttempts.computeIfAbsent(login, k -> new LoginAttempt());
        attempt.incAttempts();
    }

    @Override
    public void recordSuccessfulAttempt(String login) {
        var attempt = loginAttempts.computeIfAbsent(login, k -> new LoginAttempt());
        attempt.resetAttemptsAndUpdateTime();
    }
}
