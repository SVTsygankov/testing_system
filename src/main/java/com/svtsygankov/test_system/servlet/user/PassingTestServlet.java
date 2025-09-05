package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.service.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.util.ResponseUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.svtsygankov.test_system.listener.ContextListener.OBJECT_MAPPER;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

@WebServlet("/passing-test")
public class PassingTestServlet extends HttpServlet {
    private TestService testService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.testService = (TestService) config.getServletContext().getAttribute(TEST_SERVICE);
        this.objectMapper = (ObjectMapper) config.getServletContext().getAttribute(OBJECT_MAPPER);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            Integer testId = (Integer) req.getSession().getAttribute("currentTestId");

            if (testId == null) {
                ResponseUtils.sendErrorResponse(resp, objectMapper,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "ID теста не найден в сессии");
                return;
            }

            Test test = testService.findById(testId);

            if (test == null) {
                ResponseUtils.sendErrorResponse(resp, objectMapper,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Тест не найден");
                return;
            }

            req.setAttribute("test", test);
            req.setAttribute("contentPage", "/WEB-INF/views/secure/passing-test-content.jsp");
            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (Exception e) {
            // Централизованная обработка ошибок
            ResponseUtils.sendErrorResponse(resp, objectMapper,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage() != null ? e.getMessage() : "Внутренняя ошибка сервера");
        }
    }
}