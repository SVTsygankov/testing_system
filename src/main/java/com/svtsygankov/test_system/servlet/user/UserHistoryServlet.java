package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.entity.UserAnswer;
import com.svtsygankov.test_system.listener.ContextListener;
import com.svtsygankov.test_system.service.ResultService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/secure/history")
public class UserHistoryServlet extends HttpServlet {
    private ResultService resultService;

    @Override
    public void init() {
        this.resultService = (ResultService) getServletContext()
                .getAttribute(ContextListener.RESULT_SERVICE);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        var userId = user.getId();
        try {
            List<Result> results = resultService.getUserResults(userId);

            // Создаем список DTO или Map с дополнительным полем correctAnswersCount
            List<Map<String, Object>> resultsWithCounts = new ArrayList<>();

            for (Result result : results) {
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("result", result);

                // Конвертируем LocalDateTime в Date для JSTL
                resultData.put(
                        "dateAsDate",
                        java.util.Date.from(result.getDate().atZone(ZoneId.systemDefault()).toInstant())
                );
                // Считаем количество правильных ответов
                long correctCount = result.getAnswers().stream()
                        .filter(UserAnswer::isCorrect)
                        .count();
                resultData.put("correctAnswersCount", correctCount);
                // Считаем % правильных ответов
                int totalAnswers = result.getAnswers().size();
                int successRate = totalAnswers == 0 ? 0 : (int) Math.round((double) correctCount / totalAnswers * 100);
                resultData.put("successRate", successRate);

                resultsWithCounts.add(resultData);
            }

            req.setAttribute("resultsWithCounts", resultsWithCounts);
            req.setAttribute("contentPage", "/WEB-INF/views/secure/history-content.jsp"); // Для лейаута

            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (Exception e) {
            System.out.println("Исключение: " + e);
            req.setAttribute("error", "Error loading history");
            req.getRequestDispatcher("/WEB-INF/views/alerts.jsp")
                    .forward(req, resp);
        }
    }
}
