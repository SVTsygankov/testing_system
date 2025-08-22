package com.svtsygankov.test_system.service;

public interface LoginAttemptService {
    boolean isBlocked(String login);
    void recordFailedAttempt(String login);
    void recordSuccessfulAttempt(String login);
}
