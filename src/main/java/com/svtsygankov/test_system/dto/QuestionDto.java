package com.svtsygankov.test_system.dto;

import com.svtsygankov.test_system.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Integer id;
    private String text;
    private List<AnswerDto> answers;

    public static QuestionDto fromEntity(Question question) {
        return QuestionDto.builder()
                .id(question.getId())
                .text(question.getText())
                .answers(question.getAnswers().stream()
                        .map(AnswerDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}