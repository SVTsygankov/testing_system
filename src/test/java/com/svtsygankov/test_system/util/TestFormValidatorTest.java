package com.svtsygankov.test_system.util;

import com.svtsygankov.test_system.dto.AnswerDto;
import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.TestForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestFormValidatorTest {
    private TestFormValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TestFormValidator();
    }

    @Test
    void testValidateForCreate_NullForm_ShouldFail() {
        boolean result = validator.validateForCreate(null);

        assertFalse(result);
        assertTrue(validator.hasErrors());
        assertEquals(1, validator.getErrors().size());
        assertEquals("Форма не может быть пустой", validator.getErrors().get(0));
    }

    @Test
    void testValidateForCreate_WithId_ShouldFail() {
        TestForm form = TestForm.builder()
                .id(1)
                .title("Test")
                .topic("Topic")
                .questions(createValidQuestions())
                .build();

        boolean result = validator.validateForCreate(form);

        assertFalse(result);
        assertTrue(validator.hasErrors());
        assertTrue(validator.getErrors().contains("ID должен быть null при создании"));
    }

    @Test
    void testValidateForCreate_EmptyTitle_ShouldFail() {
        TestForm form = TestForm.builder()
                .title("")
                .topic("Topic")
                .questions(createValidQuestions())
                .build();

        boolean result = validator.validateForCreate(form);

        assertFalse(result);
        assertTrue(validator.getErrors().contains("Название теста не может быть пустым"));
    }

    @Test
    void testValidateForCreate_EmptyTopic_ShouldFail() {
        TestForm form = TestForm.builder()
                .title("Test Title")
                .topic("")
                .questions(createValidQuestions())
                .build();

        boolean result = validator.validateForCreate(form);

        assertFalse(result);
        assertTrue(validator.getErrors().contains("Не указана тема теста"));
    }

    @Test
    void testValidateForCreate_NoQuestions_ShouldFail() {
        TestForm form = TestForm.builder()
                .title("Test Title")
                .topic("Test Topic")
                .questions(new ArrayList<>())
                .build();

        boolean result = validator.validateForCreate(form);

        assertFalse(result);
        assertTrue(validator.getErrors().contains("Должен быть хотя бы один вопрос"));
    }

    @Test
    void testValidateForCreate_QuestionWithLessThan2Answers_ShouldFail() {
        QuestionDto question = QuestionDto.builder()
                .text("Question 1")
                .answers(Arrays.asList(createAnswer("Answer 1", true)))
                .build();

        TestForm form = TestForm.builder()
                .title("Test Title")
                .topic("Test Topic")
                .questions(Arrays.asList(question))
                .build();

        boolean result = validator.validateForCreate(form);

        assertFalse(result);
        assertTrue(validator.getErrors().contains(
                "У вопроса \"Question 1\" должно быть минимум 2 варианта ответа"));
    }

    @Test
    void testValidateForCreate_QuestionWithoutCorrectAnswer_ShouldFail() {
        QuestionDto question = QuestionDto.builder()
                .text("Question 1")
                .answers(Arrays.asList(
                        createAnswer("Answer 1", false),
                        createAnswer("Answer 2", false)
                ))
                .build();

        TestForm form = TestForm.builder()
                .title("Test Title")
                .topic("Test Topic")
                .questions(Arrays.asList(question))
                .build();

        boolean result = validator.validateForCreate(form);

        assertFalse(result);
        assertTrue(validator.getErrors().contains(
                "У вопроса 'Question 1' должен быть минимум 1 правильный ответ"));
    }

    @Test
    void testValidateForCreate_ValidForm_ShouldPass() {
        TestForm form = TestForm.builder()
                .title("Valid Test")
                .topic("Valid Topic")
                .questions(createValidQuestions())
                .build();

        boolean result = validator.validateForCreate(form);

        assertTrue(result);
        assertFalse(validator.hasErrors());
        assertTrue(validator.getErrors().isEmpty());
    }

    // Вспомогательные методы
    private List<QuestionDto> createValidQuestions() {
        QuestionDto question = QuestionDto.builder()
                .text("Valid Question")
                .answers(Arrays.asList(
                        createAnswer("Correct Answer", true),
                        createAnswer("Wrong Answer", false)
                ))
                .build();
        return Arrays.asList(question);
    }

    private AnswerDto createAnswer(String text, boolean correct) {
        return AnswerDto.builder()
                .text(text)
                .correct(correct)
                .build();
    }
}