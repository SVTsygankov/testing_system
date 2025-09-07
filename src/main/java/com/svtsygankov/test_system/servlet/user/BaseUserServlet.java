package com.svtsygankov.test_system.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Базовый сервлет с общими методами для всех сервлетов.
 */
public abstract class BaseUserServlet extends HttpServlet {

    /**
     * Перенаправляет на страницу ошибки с использованием layout.jsp.
     *
     * @param req         HTTP-запрос
     * @param resp        HTTP-ответ
     * @param errorMessage Сообщение об ошибке
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException      если возникает ошибка ввода-вывода
     */
    protected void forwardToErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorMessage)
            throws ServletException, IOException {
        req.setAttribute("error", errorMessage);
        req.setAttribute("contentPage", "/WEB-INF/views/alerts.jsp");
        req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);
    }

    /**
     * Перенаправляет на страницу успеха.
     *
     * @param req         HTTP-запрос
     * @param resp        HTTP-ответ
     * @param successMessage Сообщение об успехе
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException      если возникает ошибка ввода-вывода
     */
    protected void forwardToSuccessPage(HttpServletRequest req, HttpServletResponse resp, String successMessage)
            throws ServletException, IOException {
        req.setAttribute("success", successMessage);
        req.setAttribute("contentPage", "/WEB-INF/views/alerts.jsp");
        req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);
    }
}
