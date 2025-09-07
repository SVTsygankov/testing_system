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

import static com.svtsygankov.test_system.listener.ContextListener.OBJECT_MAPPER;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_FORM_VALIDATOR;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;
import static com.svtsygankov.test_system.util.ResponseUtils.sendErrorResponse;
import static com.svtsygankov.test_system.util.ResponseUtils.sendSuccessResponse;
import static com.svtsygankov.test_system.util.ResponseUtils.sendValidationErrors;

@WebServlet("/admin/test/create")
public class CreateTestServlet extends HttpServlet {
    private TestService testService;
    private TestFormValidator validator;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.testService = (TestService) getServletContext().getAttribute(TEST_SERVICE);
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        this.validator = (TestFormValidator) getServletContext().getAttribute(TEST_FORM_VALIDATOR);
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
            sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_FORBIDDEN,
                    "Пользователь не авторизован");
            return;
        }

        try {
            TestForm form = TestFormParser.parse(req, objectMapper);

            if (!validator.validateForCreate(form)) {
                sendValidationErrors(resp, objectMapper, validator.getErrors());
                return;
            }

            Test createdTest = testService.createTest(
                    form.getTitle(),
                    form.getTopic(),
                    currentUser.getId(),
                    form.getQuestions()
            );

            sendSuccessResponse(resp, objectMapper,"/admin/tests");

        } catch (Exception e) {
            sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка");
        }
    }
}
