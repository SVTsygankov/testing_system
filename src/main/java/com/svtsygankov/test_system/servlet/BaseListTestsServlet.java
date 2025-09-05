package com.svtsygankov.test_system.servlet;

import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.service.TestService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

public abstract class BaseListTestsServlet extends HttpServlet {

    private TestService testService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        testService = (TestService) config.getServletContext().getAttribute(TEST_SERVICE);
    }

    protected abstract String getContentPage();
    protected abstract String getResourceName();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, List<Test>> testsByTopic = testService.getTestsGroupedByTopic();

            req.setAttribute("testsByTopic", testsByTopic);
            req.setAttribute("contentPage", getContentPage());

            req.getRequestDispatcher(getResourceName()).forward(req, resp);
        } catch (Exception e) {
            throw new IOException("Не удалось загрузить тесты", e);
        }
    }
}
