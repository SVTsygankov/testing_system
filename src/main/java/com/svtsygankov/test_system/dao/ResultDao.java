package com.svtsygankov.test_system.dao;

import com.svtsygankov.test_system.entity.Result;

import java.util.List;
import java.util.Optional;

public interface ResultDao {
    /**
     * Сохраняет (создаёт или обновляет) результат.
     *
     * @param result Результат для сохранения.
     * @return Сохранённый результат (с присвоенным ID, если это создание).
     */
    Result save(Result result);

    /**
     * Находит результат по ID.
     *
     * @param id ID результата.
     * @return Optional с результатом или пустой, если не найден.
     */
    Optional<Result> findById(Long id);

    /**
     * Находит все результаты пользователя.
     *
     * @param userId ID пользователя.
     * @return Список результатов пользователя.
     */
    List<Result> findByUserId(Long userId);

    /**
     * Находит все результаты по ID теста.
     *
     * @param testId ID теста.
     * @return Список результатов по тесту.
     */
    List<Result> findByTestId(Integer testId);

    /**
     * Удаляет результат по ID.
     *
     * @param id ID результата.
     */
    void deleteById(Long id);

    /**
     * Получает общее количество результатов.
     *
     * @return Количество результатов.
     */
    long count();

    /**
     * Находит все результаты.
     *
     * @return Список всех результатов.
     */
    List<Result> findAll();
}