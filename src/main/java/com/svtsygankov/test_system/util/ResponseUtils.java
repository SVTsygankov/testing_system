package com.svtsygankov.test_system.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtils {

    private ResponseUtils() {}

    public static void sendValidationErrors(HttpServletResponse resp, ObjectMapper objectMapper, List<String> errors)
            throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(resp.getWriter(), response);
    }

    public static void sendErrorResponse(HttpServletResponse resp, ObjectMapper objectMapper, int status, String message)
            throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("errors", Collections.singletonList(message));
        resp.setStatus(status);
        objectMapper.writeValue(resp.getWriter(), response);
    }

    public static void sendSuccessResponse(HttpServletResponse resp, ObjectMapper objectMapper, String redirectUrl)
            throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("redirectUrl", redirectUrl);
        objectMapper.writeValue(resp.getWriter(), response);
    }

    public static void sendJsonResponse(HttpServletResponse resp, ObjectMapper objectMapper, Object data)
            throws IOException {
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), data);
    }
}