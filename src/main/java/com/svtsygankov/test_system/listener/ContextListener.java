package com.svtsygankov.test_system.listener;

import com.svtsygankov.test_system.dao.impl.ResultDaoImpl;
import com.svtsygankov.test_system.dao.impl.TestDaoImpl;
import com.svtsygankov.test_system.dao.impl.UserDaoImpl;
import com.svtsygankov.test_system.service.AuthenticationService;
import com.svtsygankov.test_system.service.LoginAttemptServiceImpl;
import com.svtsygankov.test_system.service.ResultService;
import com.svtsygankov.test_system.service.TestService;
import com.svtsygankov.test_system.service.UserService;
import com.svtsygankov.test_system.util.TestFormValidator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.SneakyThrows;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.Locale;

@WebListener
public class ContextListener implements ServletContextListener {

    public static final String AUTHENTICATION_SERVICE = "authenticationService";
    public static final String OBJECT_MAPPER = "objectMapper";
    public static final String TEST_SERVICE = "testService";
    public static final String USER_SERVICE = "userService";
    public static final String RESULT_SERVICE = "resultsService";
    public static final String TEST_FORM_VALIDATOR = "testFormValidator";

    @SneakyThrows
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        var servletContext = sce.getServletContext();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule()); // Добавляем поддержку Java 8 Time API
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Для красивого формата дат

        var userDao = new UserDaoImpl();
        var passwordEncoder = new BCryptPasswordEncoder();
        var userService = new UserService(userDao, passwordEncoder);
        var loginAttemptService = new LoginAttemptServiceImpl();
        var authenticationService = new AuthenticationService(loginAttemptService, userService);
        var testDao = new TestDaoImpl();
        var testService = new TestService(testDao);
        var resultDao = new ResultDaoImpl();
        var resultService = new ResultService(resultDao, testDao, userDao);
        var validator = new TestFormValidator();

        Locale.setDefault(new Locale("ru", "RU"));

        servletContext.setAttribute(OBJECT_MAPPER, objectMapper);
        servletContext.setAttribute(AUTHENTICATION_SERVICE, authenticationService);
        servletContext.setAttribute(USER_SERVICE, userService);
        servletContext.setAttribute(TEST_SERVICE, testService);
        servletContext.setAttribute(RESULT_SERVICE, resultService);
        servletContext.setAttribute(TEST_FORM_VALIDATOR, validator);
    }
}
