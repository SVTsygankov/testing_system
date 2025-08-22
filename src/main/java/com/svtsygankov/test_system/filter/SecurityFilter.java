package com.svtsygankov.test_system.filter;

import com.svtsygankov.test_system.entity.Role;
import com.svtsygankov.test_system.entity.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        if (user == null) {
            if (isAjaxRequest(req)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Необходимо войти");
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }

        if (path.startsWith("/secure/")) {
            // Разрешаем logout и switch-URL без проверки роли
            if (path.equals("/secure/logout") ||
                    path.equals("/secure/switch-to-admin") ||
                    path.equals("/secure/switch-to-user")) {
                chain.doFilter(request, response);
                return;
            }
            if (user.getRole() == Role.USER || user.getRole() == Role.ADMIN) {
                chain.doFilter(request, response);
                return;
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        if (path.startsWith("/admin/")) {
            if (user.getRole() == Role.ADMIN) {
                chain.doFilter(request, response);
                return;
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
               path.equals("/login") ||
               path.equals("/registration") ||
               path.startsWith("/WEB-INF") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.equals("/favicon.ico");
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}