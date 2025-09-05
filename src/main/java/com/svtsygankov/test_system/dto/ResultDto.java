package com.svtsygankov.test_system.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    private Long id;
    private Long userId;
    private Integer testId;
    private String testTitle; // Для удобства отображения
    private LocalDateTime date;
    private List<UserAnswerDto> answers;
    private int correctCount;
    private int totalCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnswerDto {
        private Long id;
        private String askedQuestion;
        private String selectedAnswer;
        private boolean correct;
        private String correctAnswer;
    }
}