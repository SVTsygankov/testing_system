package com.svtsygankov.test_system.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.dto.TestForm;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.service.TestService;
import com.svtsygankov.test_system.util.TestFormParser;
import com.svtsygankov.test_system.util.TestFormValidator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.svtsygankov.test_system.listener.ContextListener.OBJECT_MAPPER;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_FORM_VALIDATOR;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

@WebServlet("/admin/test/edit")
public class EditTestServlet extends HttpServlet {
    private TestService testService;
    private ObjectMapper objectMapper;
    private TestFormValidator validator;

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

        try {
            int testId = Integer.parseInt(req.getParameter("id"));
            Test test = testService.findById(testId);

            req.setAttribute("test", test);
            req.setAttribute("contentPage", "/WEB-INF/views/admin/edit-test.jsp");
            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный ID теста");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            // Парсинг данных формы
            TestForm form = TestFormParser.parse(req, objectMapper);

            // Валидация
            if (!validator.validateForUpdate(form)) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 статус
                resp.getWriter().write(
                        "{\"errors\": " + objectMapper.writeValueAsString(validator.getErrors()) + "}"
                );
                return;
            }

            // Получаем текущий тест для сохранения created_by
            Test existingTest = testService.findById(form.getId());
            // Обновление теста
            Test updatedTest = Test.builder()
                    .id(form.getId())
                    .title(form.getTitle())
                    .topic(form.getTopic())
                    .createdBy(existingTest.getCreatedBy())
//                    .questions(form.getQuestions())
                    .build();

            testService.updateTest(updatedTest);

            // Успешный ответ для AJAX
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\": true, \"redirectUrl\": \"/admin/tests\"}");

        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"errors\": [\"" + e.getMessage() + "\"]}");
        }
    }
}