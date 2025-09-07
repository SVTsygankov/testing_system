package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.ResultDao;
import com.svtsygankov.test_system.dto.ResultDto;
import com.svtsygankov.test_system.entity.Answer;
import com.svtsygankov.test_system.entity.Question;
import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.entity.Test;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.svtsygankov.test_system.dao.TestDao;
import com.svtsygankov.test_system.dao.UserDao;
import com.svtsygankov.test_system.entity.*;


public class ResultService {
    private final ResultDao resultDao;
    private final TestDao testDao; // Используем TestDao напрямую
    private final UserDao userDao; // Используем UserDao напрямую

    public ResultService(ResultDao resultDao, TestDao testDao, UserDao userDao) {
        this.resultDao = resultDao;
        this.testDao = testDao;
        this.userDao = userDao;
    }

    /**
     * Создаёт результат тестирования на основе ответов пользователя.
     *
     * @param userId                  ID пользователя
     * @param testId                  ID теста
     * @param questionToAnswerIndex  Карта: индекс вопроса -> индекс выбранного ответа
     * @return Сохранённый результат
     */
    public Result createTestResult(Long userId, Integer testId, Map<Integer, Integer> questionToAnswerIndex) {
        // 1. Получаем тест из базы
        Test test = testDao.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Тест с ID " + testId + " не найден"));

        // 2. Создаём сущность Result
        Result result = new Result();
        result.setUser(userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден")));
        result.setTest(test);
        result.setDate(LocalDateTime.now());

        // 3. Обрабатываем ответы
        List<Question> questions = test.getQuestions();
        for (int questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
            Question question = questions.get(questionIndex);
            Integer answerIndex = questionToAnswerIndex.get(questionIndex);

            // Находим выбранный пользователем ответ
            Answer selectedAnswer = null;
            String selectedAnswerText = "Не ответил";
            boolean isCorrect = false;

            if (answerIndex != null && answerIndex >= 0 && answerIndex < question.getAnswers().size()) {
                selectedAnswer = question.getAnswers().get(answerIndex);
                selectedAnswerText = selectedAnswer.getText();
                isCorrect = selectedAnswer.isCorrect();
            }

            // Находим правильный ответ для отображения
            String correctAnswerText = question.getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .map(Answer::getText)
                    .findFirst()
                    .orElse("Правильный ответ не указан");

            // Создаём UserAnswer
            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setAskedQuestion(question.getText());
            userAnswer.setSelectedAnswer(selectedAnswerText);
            userAnswer.setCorrect(isCorrect);
            userAnswer.setCorrectAnswer(correctAnswerText);
            userAnswer.setResult(result); // Устанавливаем связь

            result.addAnswer(userAnswer); // addAnswer устанавливает и result.setUserAnswer()
        }

        // 4. Сохраняем результат
        return resultDao.save(result);
    }

    /**
     * Получает все результаты пользователя.
     *
     * @param userId ID пользователя
     * @return Список результатов, отсортированный по дате (новые первыми)
     */
    public List<Result> getUserResults(Long userId) {
        return resultDao.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Result::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Получает результат по ID.
     *
     * @param resultId ID результата
     * @return Результат или null, если не найден
     */
    public Result getResultById(Long resultId) {
        return resultDao.findById(resultId).orElse(null);
    }

    /**
     * Получает общее количество результатов.
     *
     * @return Количество результатов
     */
    public long getTotalResultCount() {
        return resultDao.count();
    }

    // service/ResultService.java (метод toDto)

    /**
     * Преобразует сущность Result в DTO.
     *
     * @param result Сущность результата.
     * @return DTO результата.
     */
    public ResultDto toDto(Result result) {
        if (result == null) {
            return null;
        }

        // 1. Преобразуем LocalDateTime в Date для JSP
        java.util.Date dateAsDate = java.util.Date.from(
                result.getDate().atZone(ZoneId.systemDefault()).toInstant()
        );

        // 2. Преобразуем UserAnswer сущности в UserAnswerDto
        List<ResultDto.UserAnswerDto> answerDtos = result.getAnswers().stream()
                .map(answer -> ResultDto.UserAnswerDto.builder()
                        .id(answer.getId())
                        .askedQuestion(answer.getAskedQuestion())
                        .selectedAnswer(answer.getSelectedAnswer())
                        .correct(answer.isCorrect())
                        .correctAnswer(answer.getCorrectAnswer())
                        .build())
                .collect(Collectors.toList());

        // 3. Считаем правильные ответы
        int correctCount = (int) result.getAnswers().stream()
                .filter(UserAnswer::isCorrect)
                .count();

        // 4. Создаём и возвращаем DTO
        return ResultDto.builder()
                .id(result.getId())
                .userId(result.getUser().getId())
                .testId(result.getTest().getId())
                .testTitle(result.getTest().getTitle())
                .date(dateAsDate) // ← Передаём java.util.Date
                .answers(answerDtos)
                .correctCount(correctCount)
                .totalCount(result.getAnswers().size())
                .build();
    }
}