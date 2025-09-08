package com.svtsygankov.test_system.servlet.user;

import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.service.ResultService;
import com.svtsygankov.test_system.service.TestService;
import com.svtsygankov.test_system.dto.ResultDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.svtsygankov.test_system.listener.ContextListener.RESULT_SERVICE;
import static com.svtsygankov.test_system.listener.ContextListener.TEST_SERVICE;

@WebServlet("/submit-test")
public class SubmitTestServlet extends BaseUserServlet {
    private TestService testService;
    private ResultService resultService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.testService = (TestService) config.getServletContext().getAttribute(TEST_SERVICE);
        this.resultService = (ResultService) config.getServletContext().getAttribute(RESULT_SERVICE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            User user = (User) req.getSession().getAttribute("user");

            Integer testId = (Integer) req.getSession().getAttribute("currentTestId");
            if (testId == null) {
                forwardToErrorPage(req, resp, "Сессия теста не инициализирована");
                return;
            }

            Test test = testService.findById(testId);
            if (test == null) {
                forwardToErrorPage(req, resp, "Тест не найден");
                return;
            }

            Map<Integer, Integer> questionToAnswerMap = parseAnswers(req);

            Result result = resultService.createTestResult(user.getId(), testId, questionToAnswerMap);

            ResultDto resultDto = resultService.toDto(result);

            req.getSession().removeAttribute("currentTestId");

            req.setAttribute("result", resultDto);
            req.setAttribute("contentPage", "/WEB-INF/views/secure/test-result-content.jsp");
            req.getRequestDispatcher("/WEB-INF/views/layout.jsp").forward(req, resp);

        } catch (IllegalArgumentException e) {
            forwardToErrorPage(req, resp, e.getMessage());
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка при сохранении результатов";
            forwardToErrorPage(req, resp, errorMsg);
        }
    }

    /**
     * Парсит ответы пользователя из параметров запроса.
     *
     * @param request HTTP-запрос
     * @return Карта: номер вопроса -> номер ответа
     * @throws IllegalArgumentException если формат данных некорректен
     */
    private Map<Integer, Integer> parseAnswers(HttpServletRequest request) throws IllegalArgumentException {
        Map<Integer, Integer> answers = new HashMap<>();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();

            if (paramName.startsWith("question_")) {
                try {
                    // Извлекаем индекс вопроса
                    int questionIndex = Integer.parseInt(paramName.substring("question_".length()));

                    // Проверяем, что значение существует
                    if (paramValues == null || paramValues.length == 0 || paramValues[0] == null) {
                        throw new IllegalArgumentException("Отсутствует ответ на вопрос " + questionIndex);
                    }

                    // Извлекаем индекс ответа
                    int answerIndex = Integer.parseInt(paramValues[0]);
                    answers.put(questionIndex, answerIndex);

                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Неверный формат ответа для вопроса " + paramName);
                }
            }
        }

        return answers;
    }
}