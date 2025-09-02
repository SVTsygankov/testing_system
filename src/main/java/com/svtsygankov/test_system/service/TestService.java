package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.TestDao;
import com.svtsygankov.test_system.dto.TestForm;
import com.svtsygankov.test_system.entity.Answer;
import com.svtsygankov.test_system.entity.Question;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.AnswerDto;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
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

        // Добавляем вопросы, используя новый вспомогательный метод
        if (questionDtos != null && !questionDtos.isEmpty()) {
            for (QuestionDto dto : questionDtos) {
                // Создаем вопрос с ответами из DTO
                Question question = createQuestionFromDto(dto); // <-- Используем новый метод

                // Устанавливаем двустороннюю связь между Question и Test
                test.addQuestion(question); // Это внутри делает question.setTest(test)
            }
        }

        // Сохраняем тест (каскадирование сохранит вопросы и ответы)
        testDao.save(test);
        return test;
    }

    /**
     * Обновляет тест на основе данных из TestForm DTO.
     * Предполагается, что авторизация и базовая валидация уже пройдены.
     *
     * @param form DTO с обновлёнными данными теста.
     * @return Обновлённая сущность Test.
     * @throws IllegalArgumentException если тест с указанным ID не найден.
     */
    public Test updateTestFromForm(TestForm form) {

        // 1. Найти существующий тест по ID
        Optional<Test> existingTestOpt = testDao.findById(form.getId());
        if (existingTestOpt.isEmpty()) {
            throw new IllegalArgumentException("Тест с ID " + form.getId() + " не найден.");
        }

        Test existingTest = existingTestOpt.get();

        // 2. Обновить базовые поля теста
        existingTest.setTitle(form.getTitle());
        existingTest.setTopic(form.getTopic());
        // Поле createdBy НЕ обновляется, оно остаётся прежним

        // 3. Обновить вопросы и ответы
        updateQuestionsAndAnswers(existingTest, form.getQuestions());

        // 4. Сохранить обновлённый тест
        // Hibernate отслеживает изменения в managed сущностях,
        // но явный вызов save/merge может быть полезен в зависимости от настроек.
        testDao.save(existingTest);

        return existingTest;
    }
    /**
     * Внутренний метод для обновления коллекции вопросов и ответов теста.
     * Удаляет старые вопросы/ответы, добавляет новые.
     * Использует orphanRemoval=true и cascade для автоматического удаления.
     */
    private void updateQuestionsAndAnswers(Test existingTest, List<QuestionDto> formQuestions) {

        // --- Стратегия: Очистить всё и создать заново ---
        // Получаем текущие вопросы из существующего теста
        List<Question> currentQuestions = existingTest.getQuestions();

        // Создаём копию списка для безопасного итерирования
        // (чтобы избежать ConcurrentModificationException при удалении)
        List<Question> questionsToRemove = new ArrayList<>(currentQuestions);
        for (Question question : questionsToRemove) {
            // removeQuestion отвязывает вопрос от теста и удаляет его из списка.
            // orphanRemoval=true в аннотации @OneToMany в Test заставит Hibernate
            // автоматически удалить Question из БД, если он больше ни с чем не связан.
            existingTest.removeQuestion(question);
            // session.remove(question); // Обычно не нужно при orphanRemoval
        }

        // --- Добавление новых вопросов из DTO ---
        // Теперь добавляем новые вопросы из DTO, используя вспомогательный метод
        if (formQuestions != null && !formQuestions.isEmpty()) {
            for (QuestionDto questionDto : formQuestions) {
                // Создаём новую сущность вопроса (и связанные ответы) из DTO
                Question newQuestion = createQuestionFromDto(questionDto); // <-- Используем новый метод

                // Устанавливаем двустороннюю связь между новым Question и Test
                existingTest.addQuestion(newQuestion); // Это внутри делает newQuestion.setTest(existingTest)
            }
        }
    }

    /**
     * Обновление теста
     */
    public void updateTest (Test test){

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
    public Test findById (Integer id){

        Optional<Test> testOpt = testDao.findById(id);
        return testOpt.orElseThrow(() ->
                new IllegalArgumentException("Test with id " + id + " not found"));
    }

    /**
     * Удаление теста по ID
     */
    public boolean deleteById ( int id){

        Optional<Test> testOpt = testDao.findById(id);
        if (testOpt.isPresent()) {
            testDao.delete(id);
            return true;
        }
        return false;
    }

    /**
     * Создаёт сущность Question из DTO, включая связанные ответы.
     * Не устанавливает связь с Test (это делается вызывающим кодом).
     *
     * @param questionDto DTO вопроса.
     * @return Новая сущность Question.
     */
    private Question createQuestionFromDto(QuestionDto questionDto) {
        // Создаём новую сущность вопроса
        Question question = new Question(questionDto.getText());

        // Обрабатываем ответы на вопрос
        if (questionDto.getAnswers() != null && !questionDto.getAnswers().isEmpty()) {
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                // Создаём новую сущность ответа
                Answer answer = new Answer(answerDto.getText(), answerDto.isCorrect());
                // Устанавливаем двустороннюю связь между Answer и Question
                question.addAnswer(answer); // Это внутри делает answer.setQuestion(question)
            }
        }
        return question;
    }

    /**
     * Получение количества тестов
     */
    public long getTestCount () {
        return testDao.count();
    }
}
