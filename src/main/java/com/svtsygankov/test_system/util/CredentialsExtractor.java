package com.svtsygankov.test_system.util;

import com.svtsygankov.test_system.entity.Credentials;
import jakarta.servlet.http.HttpServletRequest;

public class CredentialsExtractor {
    public static Credentials extract(HttpServletRequest req) {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        return new Credentials(login, password);
    }
}
