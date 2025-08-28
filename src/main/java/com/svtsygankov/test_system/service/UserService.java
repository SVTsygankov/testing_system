package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.UserDao;
import com.svtsygankov.test_system.entity.Role;
import com.svtsygankov.test_system.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@AllArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя
     */
    public User registerUser(String login, String password, Role role) {

        // Проверка уникальности логина
        if (userDao.existsByLogin(login)) {
            throw new IllegalArgumentException("User with login '" + login + "' already exists");
        }

        User user = User.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userDao.save(user);

        return user; // commit() будет вызван в фильтре
    }

    /**
     * Поиск пользователя по логину и паролю (аутентификация)
     */
    public Optional<User> findUserByCredentials(String login, String password) {

        // Находим пользователя по логину
        Optional<User> userOpt = userDao.findByLogin(login);

        // Если пользователь найден и пароль совпадает
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return userOpt;
            }
        }

        return Optional.empty();

    }
    public boolean isExist(String login) {
        return userDao.existsByLogin(login);
    }
}
