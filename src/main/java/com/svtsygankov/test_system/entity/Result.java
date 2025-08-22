package com.svtsygankov.test_system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Result implements Entity {
    private long id;
    private long userId;
    private int testId;
    private LocalDateTime date;
    private List<UserAnswer> answers;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAnswer {
        private String askedQuestion;
        private String selectedAnswer;
        private boolean correct;
        private String correctAnswer; // ✅ Новое поле
    }

    @Override
    public long getId() {
        return id;
    }
}