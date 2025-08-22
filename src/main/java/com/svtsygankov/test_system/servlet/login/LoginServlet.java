package com.svtsygankov.test_system.servlet.login;

import com.svtsygankov.test_system.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.svtsygankov.test_system.entity.User;

@WebServlet(urlPatterns = "/login",
        initParams = {@WebInitParam(name = "resourceName", value = "/WEB-INF/views/login.jsp"),
                      @WebInitParam(name = "contentPage", value = "login-form")
        })
public class LoginServlet extends BaseAuthenticationServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (authenticationService.authenticated(req)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");

            String redirectUrl;
            if (user.getRole() == Role.ADMIN) {
                redirectUrl = "/admin/tests";
            } else {
                redirectUrl = "/secure/tests";
            }

            resp.sendRedirect(redirectUrl);

        } else {
            req.setAttribute("contentPage", "/WEB-INF/views/"+
                    getInitParameter("contentPage") + ".jsp");
            req.getRequestDispatcher(getInitParameter("resourceName")).forward(req, resp);
        }
    }
}
