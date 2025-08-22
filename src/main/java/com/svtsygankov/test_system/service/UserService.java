package com.svtsygankov.test_system.service;

import com.svtsygankov.test_system.dao.UserDao;
import com.svtsygankov.test_system.entity.Role;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.util.HibernateUtil;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Проверка уникальности логина
            if (userDao.existsByLogin(session, login)) {
                throw new IllegalArgumentException("User with login '" + login + "' already exists");
            }
            User user = User.builder()
                    .login(login)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build();

            userDao.save(session, user);

            transaction.commit();
            return user;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error registering user: " + e.getMessage(), e);
        }
    }

    /**
     * Поиск пользователя по логину и паролю (аутентификация)
     */
    public Optional<User> findUserByCredentials(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Находим пользователя по логину
            Optional<User> userOpt = userDao.findByLogin(session, login);

            // Если пользователь найден и пароль совпадает
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(password, user.getPassword())) {
                    return userOpt;
                }
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Error authenticating user: " + e.getMessage(), e);
        }
    }
    public boolean isExist(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return userDao.existsByLogin(session, login);
        } catch (Exception e) {
            throw new RuntimeException("Error checking user existence: " + e.getMessage(), e);
        }
    }

}
