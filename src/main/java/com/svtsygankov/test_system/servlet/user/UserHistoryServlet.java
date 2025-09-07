package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.listener.ContextListener;
import com.svtsygankov.test_system.service.ResultService;
import com.svtsygankov.test_system.dto.ResultDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/secure/history")
public class UserHistoryServlet extends BaseUserServlet {
    private ResultService resultService;

    @Override
    public void init() throws ServletException {
        this.resultService = (ResultService) getServletContext()
                .getAttribute(ContextListener.RESULT_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            User user = (User) req.getSession().getAttribute("user");

            List<Result> results = resultService.getUserResults(user.getId());

            List<ResultDto> resultDtos = results.stream()
                    .map(resultService::toDto) // ← Используем метод сервиса
                    .collect(Collectors.toList());

            req.setAttribute("results", resultDtos); // ← Передаем список DTO
            req.setAttribute("contentPage", "/WEB-INF/views/secure/history-content.jsp");
            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (Exception e) {
            forwardToErrorPage(req, resp, "Ошибка загрузки истории: " + e.getMessage());
        }
    }
}
