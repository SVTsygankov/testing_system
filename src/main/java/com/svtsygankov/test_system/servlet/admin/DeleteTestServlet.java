package com.svtsygankov.test_system.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.service.TestService;
import com.svtsygankov.test_system.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import static com.svtsygankov.test_system.listener.ContextListener.OBJECT_MAPPER;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

@WebServlet("/admin/test/delete")
public class DeleteTestServlet extends HttpServlet{

    private TestService testService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.testService = (TestService) getServletContext().getAttribute(TEST_SERVICE);
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute(OBJECT_MAPPER);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_BAD_REQUEST,
                        "ID теста обязателен");
                return;
            }

            int testId = Integer.parseInt(idParam);

            if (!testService.deleteById(testId)) {
                ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_NOT_FOUND,
                        "Тест не найден");
                return;
            }

            // Успешный ответ
            ResponseUtils.sendSuccessResponse(resp, objectMapper, "/admin/tests");

        } catch (NumberFormatException e) {
            ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_BAD_REQUEST,
                    "Неверный формат ID теста");
        } catch (Exception e) {
            ResponseUtils.sendErrorResponse(resp, objectMapper, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка");
        }
    }
}
