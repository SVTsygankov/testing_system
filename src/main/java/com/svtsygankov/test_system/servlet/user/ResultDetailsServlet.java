package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.listener.ContextListener;
import com.svtsygankov.test_system.service.ResultService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneId;

@WebServlet("/secure/result-details")
public class ResultDetailsServlet extends HttpServlet {
    private ResultService resultService;

    @Override
    public void init() {
        this.resultService = (ResultService) getServletContext()
                .getAttribute(ContextListener.RESULT_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Получаем ID результата
        String idParam = req.getParameter("id");
        if (idParam == null || !idParam.matches("\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid result ID");
            return;
        }
        Long resultId = Long.parseLong(idParam);

        // 2. Получаем пользователя из сессии
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("/login");
            return;
        }

        // 3. Получаем Result по ID
        Result result;
        try {
            result = resultService.getResultById(resultId);
        } catch (Exception e) {
            req.setAttribute("error", "Результат не найден");
            req.getRequestDispatcher("/WEB-INF/views/alerts.jsp").forward(req, resp);
            return;
        }

        // 4. Проверяем, что этот результат принадлежит пользователю
//        if (result.getUserId() != user.getId()) {
//            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
//            return;
//        }

        // 5. Передаём в JSP
        req.setAttribute("result", result);

        // Добавим дату в формате java.util.Date
        java.util.Date dateAsDate = java.util.Date.from(
                result.getDate().atZone(ZoneId.systemDefault()).toInstant()
        );
        req.setAttribute("resultDateAsDate", dateAsDate);

        req.setAttribute("contentPage", "/WEB-INF/views/secure/result-details.jsp");
        req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);
    }
}