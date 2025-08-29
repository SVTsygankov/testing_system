package com.svtsygankov.test_system.util;

import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.AnswerDto;
import com.svtsygankov.test_system.dto.TestForm;

import java.util.ArrayList;
import java.util.List;

public class TestFormValidator {
    private final List<String> errors = new ArrayList<>(); // Инициализируем сразу

    public boolean validateForCreate(TestForm form) {
        errors.clear();

        if (form == null) {
            errors.add("Форма не может быть пустой");
            return false;
        }

        if (form.getId() != null) {
            errors.add("ID должен быть null при создании");
        }

        validateCommon(form);
        return errors.isEmpty();
    }

    public boolean validateForUpdate(TestForm form) {
        errors.clear();

        if (form == null) {
            errors.add("Форма не может быть пустой");
            return false;
        }

        if (form.getId() == null) {
            errors.add("ID теста обязательно");
        }

        validateCommon(form);
        return errors.isEmpty();
    }

    private void validateCommon(TestForm form) {

        if (isNullOrEmpty(form.getTitle())) {
            errors.add("Название теста не может быть пустым");
        }

        if (isNullOrEmpty(form.getTopic())) {
            errors.add("Не указана тема теста");
        }

        if (form.getQuestions() == null || form.getQuestions().isEmpty()) {
            errors.add("Должен быть хотя бы один вопрос");
            return;
        }

        for (int i = 0; i < form.getQuestions().size(); i++) {
            QuestionDto question = form.getQuestions().get(i);
            validateQuestion(question, i + 1);
        }
    }

    private void validateQuestion(QuestionDto question, int questionNumber) {
        if (question == null) {
            errors.add(String.format("Вопрос %d не может быть пустым", questionNumber));
            return;
        }

        if (isNullOrEmpty(question.getText())) {
            errors.add(String.format("Вопрос %d: текст вопроса не может быть пустым", questionNumber));
            return;
        }

        if (question.getAnswers() == null || question.getAnswers().size() < 2) {
            errors.add(String.format("У вопроса \"%s\" должно быть минимум 2 варианта ответа",
                    question.getText()));
            return;
        }

        boolean hasCorrect = question.getAnswers().stream()
                .anyMatch(AnswerDto::isCorrect);
        if (!hasCorrect) {
            errors.add(String.format("У вопроса '%s' должен быть минимум 1 правильный ответ",
                    question.getText()));
        }

        for (int j = 0; j < question.getAnswers().size(); j++) {
            AnswerDto answer = question.getAnswers().get(j);
            validateAnswer(answer, question.getText(), j + 1);
        }
    }

    private void validateAnswer(AnswerDto answer, String questionText, int answerNumber) {
        if (answer == null) {
            errors.add(String.format("Вопрос \"%s\", ответ %d не может быть пустым",
                    questionText, answerNumber));
            return;
        }

        if (isNullOrEmpty(answer.getText())) {
            errors.add(String.format("Вопрос \"%s\", ответ %d: текст ответа не может быть пустым",
                    questionText, answerNumber));
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors); // Возвращаем копию для безопасности
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}