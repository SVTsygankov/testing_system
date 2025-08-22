package com.svtsygankov.test_system.listener;

import com.svtsygankov.test_system.dao.impl.UserDaoImpl;
import com.svtsygankov.test_system.service.AuthenticationService;
import com.svtsygankov.test_system.service.LoginAttemptServiceImpl;
import com.svtsygankov.test_system.service.UserService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.SneakyThrows;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Locale;

@WebListener
public class ContextListener implements ServletContextListener {

    public static final String AUTHENTICATION_SERVICE = "authenticationService";
    public static final String TEST_SERVICE = "testService";
    public static final String USER_SERVICE = "userService";
    public static final String RESULTS_SERVICE = "resultsService";

    @SneakyThrows
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        var servletContext = sce.getServletContext();

        var userDao = new UserDaoImpl();
        var passwordEncoder = new BCryptPasswordEncoder();
        var userService = new UserService(userDao, passwordEncoder);
        var loginAttemptService = new LoginAttemptServiceImpl();
        var authenticationService = new AuthenticationService(loginAttemptService, userService);

        Locale.setDefault(new Locale("ru", "RU"));

        servletContext.setAttribute(AUTHENTICATION_SERVICE, authenticationService);
        servletContext.setAttribute(USER_SERVICE, userService);
    }
}
