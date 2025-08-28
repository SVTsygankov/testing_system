package com.svtsygankov.test_system.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.dto.TestForm;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class TestFormParser {

    public static TestForm parse(HttpServletRequest req, ObjectMapper objectMapper) throws IOException {
        // Парсим JSON напрямую в DTO
        return objectMapper.readValue(req.getReader(), TestForm.class);
    }
}