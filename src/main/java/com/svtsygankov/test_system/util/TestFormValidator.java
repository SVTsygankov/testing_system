package com.svtsygankov.test_system.util;

import com.svtsygankov.test_system.dto.QuestionDto;
import com.svtsygankov.test_system.dto.AnswerDto;
import com.svtsygankov.test_system.dto.TestForm;

import java.util.ArrayList;
import java.util.List;

public class TestFormValidator {
    private List<String> errors;

    // Добавляем метод для ручной очистки
    public void clearErrors() {
        this.errors = new ArrayList<>();
    }

    public boolean validateForCreate(TestForm form) {
        clearErrors(); // Очищаем перед каждой валидацией
        if (form.getId() != null) {
            errors.add("ID должен быть null при создании");
        }
        validateCommon(form);
        return errors.isEmpty();
    }

    public boolean validateForUpdate(TestForm form) {
        clearErrors(); // Очищаем перед каждой валидацией
        if (form.getId() == null) {
            errors.add("ID теста обязательно");
        }
        validateCommon(form);
        return errors.isEmpty();
    }

    private void validateCommon(TestForm form) {
        if (form.getTitle() == null || form.getTitle().trim().isEmpty()) {
            errors.add("Название теста не может быть пустым");
        }

        if (form.getTopic() == null || form.getTopic().isBlank()) {
            errors.add("Не указана тема теста");
        }

        if (form.getQuestions().isEmpty()) {
            errors.add("Должен быть хотя бы один вопрос");
            return;
        }

        for (QuestionDto question : form.getQuestions()) {
            if (question.getAnswers().size() < 2) {
                errors.add("У вопроса \"" + question.getText() + "\" должно быть минимум 2 варианта ответа");
                continue;
            }

            boolean hasCorrect = question.getAnswers().stream()
                    .anyMatch(AnswerDto::isCorrect);
            if (!hasCorrect) {
                errors.add(String.format("У вопроса '%s' должен быть минимум 1 правильный ответ",
                        question.getText() != null ? question.getText() : ""));
            }
        }
    }

    public List<String> getErrors() {
        return errors;
    }
}