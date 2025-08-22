package com.svtsygankov.test_system.servlet.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/registration",
        initParams = {@WebInitParam(name = "resourceName", value = "/WEB-INF/views/registration.jsp"),
                      @WebInitParam(name = "contentPage", value = "registration-form")
        })
public class RegistrationServlet extends BaseAuthenticationServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(authenticationService.register(req)) {
            resp.sendRedirect("/login");
        } else {
            req.setAttribute("contentPage", "/WEB-INF/views/registration-form.jsp");
            req.getRequestDispatcher(getInitParameter("resourceName")).forward(req, resp);
        }
    }
}
