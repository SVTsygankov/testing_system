package com.svtsygankov.test_system.dto;

import com.svtsygankov.test_system.entity.Test;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {
    private Integer id;
    private String title;
    private String topic;
    private Long createdBy;
    private List<QuestionDto> questions;

    public static TestDto fromEntity(Test test) {
        return TestDto.builder()
                .id(test.getId())
                .title(test.getTitle())
                .topic(test.getTopic())
                .createdBy(test.getCreatedBy())
                .questions(test.getQuestions().stream()
                        .map(QuestionDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}