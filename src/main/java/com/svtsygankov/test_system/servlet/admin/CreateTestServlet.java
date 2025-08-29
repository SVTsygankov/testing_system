package com.svtsygankov.test_system.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.dto.TestForm;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.service.TestService;
import com.svtsygankov.test_system.util.TestFormParser;
import com.svtsygankov.test_system.util.TestFormValidator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.svtsygankov.test_system.listener.ContextListener.OBJECT_MAPPER;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_FORM_VALIDATOR;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

@WebServlet("/admin/test/create")
public class CreateTestServlet extends HttpServlet {
    private TestService testService;
    private TestFormValidator validator;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.testService = (TestService) config.getServletContext().getAttribute(TEST_SERVICE);
        this.objectMapper = (ObjectMapper) config.getServletContext().getAttribute(OBJECT_MAPPER);
        this.validator = (TestFormValidator) config.getServletContext().getAttribute(TEST_FORM_VALIDATOR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Создаем пустой тест для формы
        Test emptyTest = Test.builder()
                .title("")
                .topic("")
                .questions(new ArrayList<>())
                .build();

        req.setAttribute("test", emptyTest);
        req.setAttribute("contentPage", "/WEB-INF/views/admin/create-test.jsp");
        req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        // Проверка авторизации
        if (currentUser == null) {
            sendErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    "Пользователь не авторизован");
            return;
        }

        try {
            TestForm form = TestFormParser.parse(req, objectMapper);

            if (!validator.validateForCreate(form)) {
                sendValidationErrors(resp, validator.getErrors());
                return;
            }

            Test createdTest = testService.createTest(
                    form.getTitle(),
                    form.getTopic(),
                    currentUser.getId(),
                    form.getQuestions()
            );

            sendSuccessResponse(resp, "/admin/tests");

        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка");
        }
    }

    private void sendValidationErrors(HttpServletResponse resp, List<String> errors) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(resp.getWriter(), errorResponse);
    }

    private void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", Collections.singletonList(message));
        resp.setStatus(status);
        objectMapper.writeValue(resp.getWriter(), errorResponse);
    }

    private void sendSuccessResponse(HttpServletResponse resp, String redirectUrl) throws IOException {
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("redirectUrl", redirectUrl);
        objectMapper.writeValue(resp.getWriter(), successResponse);
    }
}
