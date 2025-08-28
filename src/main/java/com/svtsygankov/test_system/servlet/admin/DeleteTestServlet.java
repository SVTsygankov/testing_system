package com.svtsygankov.test_system.servlet.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svtsygankov.test_system.service.TestService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static com.svtsygankov.test_system.listener.ContextListener.OBJECT_MAPPER;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

@WebServlet("/admin/test/delete")
public class DeleteTestServlet extends HttpServlet{

    private TestService testService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.testService = (TestService) config.getServletContext().getAttribute(TEST_SERVICE);
        this.objectMapper = (ObjectMapper) config.getServletContext().getAttribute(OBJECT_MAPPER);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            int testId = Integer.parseInt(req.getParameter("id"));

            if (!testService.deleteById(testId)) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Тест не найден\"}");
                return;
            }

            out.write("{\"success\":true, \"redirectUrl\":\"/admin/tests\"}");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Неверный формат ID теста\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Ошибка сервера: " + e.getMessage() + "\"}");
        }
    }
}
