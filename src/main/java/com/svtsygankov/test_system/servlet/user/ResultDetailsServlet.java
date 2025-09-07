package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.dto.ResultDto;
import com.svtsygankov.test_system.listener.ContextListener;
import com.svtsygankov.test_system.service.ResultService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/secure/result-details")
public class ResultDetailsServlet extends BaseUserServlet {
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

            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                forwardToErrorPage(req, resp, "ID результата обязателен");
                return;
            }

            long resultId;
            try {
                resultId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                forwardToErrorPage(req, resp, "Неверный формат ID результата");
                return;
            }

            Result result = resultService.getResultById(resultId);
            if (result == null) {
                forwardToErrorPage(req, resp, "Результат не найден");
                return;
            }

            ResultDto resultDto = resultService.toDto(result);

            // 6. Передаем в JSP
            req.setAttribute("result", resultDto);
            req.setAttribute("contentPage", "/WEB-INF/views/secure/result-details.jsp");
            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (Exception e) {
            forwardToErrorPage(req, resp, "Ошибка загрузки истории: " + e.getMessage());
        }
    }
}