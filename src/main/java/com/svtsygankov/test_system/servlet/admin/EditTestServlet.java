package com.svtsygankov.test_system.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.dto.TestForm;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.service.TestService;
import com.svtsygankov.test_system.util.ResponseUtils;
import com.svtsygankov.test_system.util.TestFormParser;
import com.svtsygankov.test_system.util.TestFormValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
    public void init() throws ServletException {
        this.testService = (TestService) getServletContext().getAttribute(TEST_SERVICE);
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
        this.validator = (TestFormValidator) getServletContext().getAttribute(TEST_FORM_VALIDATOR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID теста обязателен");
                return;
            }

            int testId = Integer.parseInt(idParam);
            Test test = testService.findById(testId);

            if (test == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Тест не найден");
                return;
            }

            req.setAttribute("test", test);
            req.setAttribute("contentPage", "/WEB-INF/views/admin/edit-test.jsp");
            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID теста");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка загрузки теста");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        try {
            // Парсинг данных формы
            TestForm form = TestFormParser.parse(req, objectMapper);

            // Валидация
            if (!validator.validateForUpdate(form)) {
                ResponseUtils.sendValidationErrors(resp, objectMapper, validator.getErrors());
                return;
            }

            // Всю логику переносим в сервис
            Test updatedTest = testService.updateTestFromForm(form);
            ResponseUtils.sendSuccessResponse(resp, objectMapper, "/admin/tests");


        } catch (NumberFormatException e) {
            ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_BAD_REQUEST,
                    "Неверный формат данных");

        } catch (IllegalArgumentException e) {
            ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage());

        } catch (Exception e) {
            ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка");
        }
    }
}