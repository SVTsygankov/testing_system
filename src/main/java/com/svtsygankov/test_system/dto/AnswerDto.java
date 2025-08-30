package com.svtsygankov.test_system.dto;

import com.svtsygankov.test_system.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private Integer id; // Добавляем ID, если нужно на фронтенде
    private String text;
    private boolean correct;

    public static AnswerDto fromEntity(Answer answer) {
        if (answer == null) {
            return null;
        }
        return AnswerDto.builder()
                .id(answer.getId()) // Передаём ID если требуется
                .text(answer.getText())
                .correct(answer.isCorrect())
                .build();
    }
}