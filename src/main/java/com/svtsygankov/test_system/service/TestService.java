package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.TestDao;
import com.svtsygankov.test_system.dto.TestForm;
import com.svtsygankov.test_system.entity.Answer;
import com.svtsygankov.test_system.entity.Question;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.AnswerDto;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Получение всех тестов отсортированных по темам
     */

    public Map<String, List<Test>> getTestsGroupedByTopic() {
        List<Test> allTests = testDao.findAll();
        return allTests.stream()
                .collect(Collectors.groupingBy(Test::getTopic));
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
    // Новый подход: точное обновление
    public Test updateTestFromForm(TestForm form) {

        Optional<Test> existingTestOpt = testDao.findById(form.getId());
        if (existingTestOpt.isEmpty()) {
            throw new IllegalArgumentException("Тест с ID " + form.getId() + " не найден.");
        }

        Test existingTest = existingTestOpt.get();

        existingTest.setTitle(form.getTitle());
        existingTest.setTopic(form.getTopic());

        updateQuestionsExactly(existingTest, form.getQuestions());

        // 4. Сохранить обновлённый тест
        // При использовании getCurrentSession() и открытой транзакции в фильтре,
        // Hibernate автоматически отслеживает изменения.
        // Явный вызов save/merge может быть не обязателен, но не повредит.
        testDao.save(existingTest);

        return existingTest;

    }

    /**
     * Точное обновление коллекции вопросов и ответов теста.
     * Сопоставляет существующие сущности с DTO и выполняет UPDATE/INSERT/DELETE только для изменений.
     */
    private void updateQuestionsExactly(Test existingTest, List<QuestionDto> formQuestions) {
        // Получаем текущие вопросы из БД (уже загружены, если используется LAZY/EAGER или были доступны)
        List<Question> currentQuestions = existingTest.getQuestions();

        // Создаем карты для быстрого поиска
        Map<Integer, Question> currentQuestionMap = currentQuestions.stream()
                .filter(q -> q.getId() != null) // Только сохраненные вопросы
                .collect(Collectors.toMap(Question::getId, q -> q));

        Set<Integer> formQuestionIds = formQuestions.stream()
                .map(QuestionDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // --- Удаление вопросов, которых нет в DTO ---
        List<Question> questionsToRemove = currentQuestions.stream()
                .filter(q -> q.getId() == null || !formQuestionIds.contains(q.getId()))
                .collect(Collectors.toList());

        for (Question question : questionsToRemove) {
            existingTest.removeQuestion(question); // Это удалит из списка и установит question.setTest(null)
            // orphanRemoval=true в Test заставит Hibernate удалить Question из БД
        }

        // --- Обработка вопросов из DTO ---
        for (int i = 0; i < formQuestions.size(); i++) {
            QuestionDto questionDto = formQuestions.get(i);
            Question questionEntity;

            if (questionDto.getId() != null && currentQuestionMap.containsKey(questionDto.getId())) {
                // 1. Обновление существующего вопроса
                questionEntity = currentQuestionMap.get(questionDto.getId());
                questionEntity.setText(questionDto.getText());
            } else {
                // 2. Создание нового вопроса
                questionEntity = new Question(questionDto.getText());
                existingTest.addQuestion(questionEntity); // Устанавливает question.setTest(existingTest)
            }

            // --- Обновление ответов для вопроса ---
            updateAnswersExactly(questionEntity, questionDto.getAnswers());
        }
    }

    /**
     * Точное обновление коллекции ответов для вопроса.
     */
    private void updateAnswersExactly(Question questionEntity, List<AnswerDto> formAnswers) {
        List<Answer> currentAnswers = questionEntity.getAnswers();

        // Создаем карты для быстрого поиска
        Map<Integer, Answer> currentAnswerMap = currentAnswers.stream()
                .filter(a -> a.getId() != null)
                .collect(Collectors.toMap(Answer::getId, a -> a));

        Set<Integer> formAnswerIds = formAnswers.stream()
                .map(AnswerDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // --- Удаление ответов, которых нет в DTO ---
        List<Answer> answersToRemove = currentAnswers.stream()
                .filter(a -> a.getId() == null || !formAnswerIds.contains(a.getId()))
                .collect(Collectors.toList());

        for (Answer answer : answersToRemove) {
            questionEntity.removeAnswer(answer); // Удаляет из списка и устанавливает answer.setQuestion(null)
            // orphanRemoval=true в Question заставит Hibernate удалить Answer из БД
        }

        // --- Обработка ответов из DTO ---
        for (int i = 0; i < formAnswers.size(); i++) {
            AnswerDto answerDto = formAnswers.get(i);
            Answer answerEntity;

            if (answerDto.getId() != null && currentAnswerMap.containsKey(answerDto.getId())) {
                // 1. Обновление существующего ответа
                answerEntity = currentAnswerMap.get(answerDto.getId());
                answerEntity.setText(answerDto.getText());
                answerEntity.setCorrect(answerDto.isCorrect());
            } else {
                // 2. Создание нового ответа
                answerEntity = new Answer(answerDto.getText(), answerDto.isCorrect());
                questionEntity.addAnswer(answerEntity); // Устанавливает answer.setQuestion(questionEntity)
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
