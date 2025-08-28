package com.svtsygankov.test_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestForm {
    private Integer id;
    private String title;
    private String topic;
    private List<QuestionDto> questions;
}