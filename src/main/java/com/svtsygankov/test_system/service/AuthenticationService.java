package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static com.svtsygankov.test_system.util.CredentialsExtractor.extract;

@RequiredArgsConstructor
public class AuthenticationService {

    private final LoginAttemptService loginAttemptService;
    private final UserService userService;

    public boolean register(HttpServletRequest req) throws IOException {
        var credentials = extract(req);
        String confirmPassword = req.getParameter("confirmPassword");

        if (!credentials.password().equals(confirmPassword)) {
            req.setAttribute("registrationErrorMessage", "Пароли не совпадают");
            return false;
        }

        if (userService.isExist(credentials.login())) {
            req.setAttribute("registrationErrorMessage", "Имя пользователя уже занято");
            return false;
        }

        userService.registerUser(credentials.login(), credentials.password(), Role.USER);
        return true;
    }

    public boolean authenticated(HttpServletRequest req) throws IOException {

        var credentials = extract(req);

        if (loginAttemptService.isBlocked(credentials.login())) {
//            resp.sendError(429); // 429
            req.setAttribute("authMessageError", "User is blocked (2 min)");
            return false;
        }

        var optionalUser = userService.findUserByCredentials(credentials.login(), credentials.password());

        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            loginAttemptService.recordSuccessfulAttempt(credentials.login());
            req.getSession().setAttribute("user", user);
            return true;

        } else {
            loginAttemptService.recordFailedAttempt(credentials.login());
            return false;
        }
    }
}
