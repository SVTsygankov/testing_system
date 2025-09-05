package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.TestDao;
import com.svtsygankov.test_system.dto.AnswerDto;
import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.TestForm;
import com.svtsygankov.test_system.entity.Answer;
import com.svtsygankov.test_system.entity.Question;
import com.svtsygankov.test_system.entity.Test;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class) // Позволяет использовать аннотации @Mock, @InjectMocks
class TestServiceTest {

    @Mock
    private TestDao testDao;

    @InjectMocks
    private TestService testService; // Сервис, который тестируем

    private Test sampleTest;
    private Question sampleQuestion;
    private Answer sampleAnswer;

    @BeforeEach
    void setUp() {
        // Создаём образцы сущностей для тестов
        sampleTest = new Test();
        sampleTest.setId(1);
        sampleTest.setTitle("Sample Test");
        sampleTest.setTopic("Sample Topic");
        sampleTest.setCreatedBy(1L);

        sampleQuestion = new Question("Sample Question?");
        sampleQuestion.setId(10);
        sampleQuestion.setTest(sampleTest);

        sampleAnswer = new Answer("Sample Answer", true);
        sampleAnswer.setId(100);
        sampleAnswer.setQuestion(sampleQuestion);

        sampleQuestion.setAnswers(new ArrayList<>(Collections.singletonList(sampleAnswer)));
        sampleTest.setQuestions(new ArrayList<>(Collections.singletonList(sampleQuestion)));
    }

    // --- Тесты для findAll ---
    @org.junit.jupiter.api.Test
    void findAll_ReturnsListOfTests() {
        List<Test> mockTests = Arrays.asList(new Test(), new Test());

        when(testDao.findAll()).thenReturn(mockTests);

        List<Test> result = testService.findAll();

        assertThat(result).hasSize(2);
        verify(testDao).findAll();
    }

    // --- Тесты для findById ---
    @org.junit.jupiter.api.Test
    void findById_ExistingId_ReturnsTest() {
        int testId = 1;
        Test mockTest = new Test();
        mockTest.setId(testId);

        when(testDao.findById(testId)).thenReturn(Optional.of(mockTest));

        Test result = testService.findById(testId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        verify(testDao).findById(testId);
    }

    @org.junit.jupiter.api.Test
    void findById_NonExistingId_ThrowsException() {
        int testId = 999;

        when(testDao.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testService.findById(testId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Test with id " + testId + " not found");

        verify(testDao).findById(testId);
    }

    // --- Тесты для deleteById ---
    @org.junit.jupiter.api.Test
    void deleteById_ExistingId_ReturnsTrue() {
        int testId = 1;
        Test mockTest = new Test();

        when(testDao.findById(testId)).thenReturn(Optional.of(mockTest));

        boolean result = testService.deleteById(testId);

        assertThat(result).isTrue();
        verify(testDao).findById(testId);
        verify(testDao).delete(testId);
    }

    @org.junit.jupiter.api.Test
    void deleteById_NonExistingId_ReturnsFalse() {
        int testId = 999;

        when(testDao.findById(testId)).thenReturn(Optional.empty());

        boolean result = testService.deleteById(testId);

        assertThat(result).isFalse();
        verify(testDao).findById(testId);
        verify(testDao, never()).delete(anyInt()); // Убедиться, что delete НЕ вызывался
    }

    // --- Тесты для createTest ---
    @org.junit.jupiter.api.Test
    void createTest_ValidData_CreatesAndSavesTest() {
        String title = "New Test";
        String topic = "New Topic";
        Long authorId = 2L;

        AnswerDto answerDto = AnswerDto.builder()
                .text("Answer 1")
                .correct(true)
                .build();

        QuestionDto questionDto = QuestionDto.builder()
                .text("Question 1?")
                .answers(Arrays.asList(answerDto))
                .build();

        List<QuestionDto> questionDtos = Arrays.asList(questionDto);

        Test createdTest = testService.createTest(title, topic, authorId, questionDtos);

        assertThat(createdTest).isNotNull();
        assertThat(createdTest.getTitle()).isEqualTo(title);
        assertThat(createdTest.getTopic()).isEqualTo(topic);
        assertThat(createdTest.getCreatedBy()).isEqualTo(authorId);
        assertThat(createdTest.getQuestions()).hasSize(1);

        Question createdQuestion = createdTest.getQuestions().get(0);
        assertThat(createdQuestion.getText()).isEqualTo("Question 1?");
        assertThat(createdQuestion.getTest()).isEqualTo(createdTest); // Проверка двусторонней связи

        assertThat(createdQuestion.getAnswers()).hasSize(1);
        Answer createdAnswer = createdQuestion.getAnswers().get(0);
        assertThat(createdAnswer.getText()).isEqualTo("Answer 1");
        assertThat(createdAnswer.isCorrect()).isTrue();
        assertThat(createdAnswer.getQuestion()).isEqualTo(createdQuestion); // Проверка двусторонней связи

        verify(testDao).save(any(Test.class)); // Проверка, что save был вызван
    }

    // --- Тесты для updateTestFromForm (основной метод) ---
    @org.junit.jupiter.api.Test
    void updateTestFromForm_ValidForm_UpdatesTestFields() {
        // Arrange: Подготавливаем существующий тест в DAO
        when(testDao.findById(1)).thenReturn(Optional.of(sampleTest));

        // Создаём DTO с обновлёнными данными
        TestForm updateForm = new TestForm();
        updateForm.setId(1);
        updateForm.setTitle("Updated Title");
        updateForm.setTopic("Updated Topic");
        updateForm.setQuestions(new ArrayList<>());
        // Для простоты в этом тесте не обновляем вопросы/ответы

        // Act
        Test updatedTest = testService.updateTestFromForm(updateForm);

        // Assert
        assertThat(updatedTest).isNotNull();
        assertThat(updatedTest.getId()).isEqualTo(1);
        assertThat(updatedTest.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTest.getTopic()).isEqualTo("Updated Topic");
        // createdBy должен остаться прежним
        assertThat(updatedTest.getCreatedBy()).isEqualTo(1L);

        verify(testDao).findById(1);
        verify(testDao).save(updatedTest); // Проверяем, что save был вызван с обновлённым тестом
    }

    @org.junit.jupiter.api.Test
    void updateTestFromForm_NonExistingId_ThrowsException() {
        int nonExistingId = 999;
        TestForm updateForm = new TestForm();
        updateForm.setId(nonExistingId);

        when(testDao.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testService.updateTestFromForm(updateForm))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Тест с ID " + nonExistingId + " не найден.");

        verify(testDao).findById(nonExistingId);
        verify(testDao, never()).save(any(Test.class)); // Убедиться, что save НЕ вызывался
    }

    // --- Тесты для getTestCount ---
    @org.junit.jupiter.api.Test
    void getTestCount_ReturnsCountFromDao() {
        long expectedCount = 5L;
        when(testDao.count()).thenReturn(expectedCount);

        long result = testService.getTestCount();

        assertThat(result).isEqualTo(expectedCount);
        verify(testDao).count();
    }
}
