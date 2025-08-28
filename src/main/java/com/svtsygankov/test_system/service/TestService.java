package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.TestDao;
import com.svtsygankov.test_system.entity.Answer;
import com.svtsygankov.test_system.entity.Question;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.AnswerDto;

import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class TestService {

    private final TestDao testDao;

    /**
     * Получение всех тестов
     */
    public List<Test> findAll() {
        return testDao.findAll();
    }

    /**
     * Создание нового теста с вопросами и ответами
     */
    public Test createTest(String title, String topic, Long authorId, List<QuestionDto> questionDtos) {
        // Создаем тест
        Test test = Test.builder()
                .title(title)
                .topic(topic)
                .createdBy(authorId)
                .build();

        for (QuestionDto dto : questionDtos) {
            Question question = new Question(dto.getText());

            // Создаём ответы из DTO
            for (AnswerDto answerDto : dto.getAnswers()) {
                Answer answer = new Answer(answerDto.getText(), answerDto.isCorrect());
                question.addAnswer(answer);
            }

            test.addQuestion(question);
        }

        // Сохраняем тест (каскадирование сохранит вопросы и ответы)
        testDao.save(test);
        return test;
    }

    /**
     * Обновление теста
     */
    public void updateTest(Test test) {

        // Проверяем существование теста
        Optional<Test> existingTest = testDao.findById(test.getId());
        if (existingTest.isEmpty()) {
            throw new IllegalArgumentException("Test with id " + test.getId() + " not found");
        }

        testDao.save(test);
    }

    /**
     * Поиск теста по ID
     */
    public Test findById(int id) {

        Optional<Test> testOpt = testDao.findById(id);
        return testOpt.orElseThrow(() ->
                new IllegalArgumentException("Test with id " + id + " not found"));
    }

    /**
     * Удаление теста по ID
     */
    public boolean deleteById(int id) {

        Optional<Test> testOpt = testDao.findById(id);
        if (testOpt.isPresent()) {
            testDao.delete(id);
            return true;
        }
        return false;
    }

    /**
     * Получение количества тестов
     */
    public long getTestCount() {
        return testDao.count();
    }
}