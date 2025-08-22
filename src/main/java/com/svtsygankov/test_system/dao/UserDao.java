package com.svtsygankov.test_system.dao;

import com.svtsygankov.test_system.entity.User;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(Session session, User user);
    Optional<User> findById(Session session, Long id);
    Optional<User> findByLogin(Session session, String login);
    List<User> findAll(Session session);
    boolean existsByLogin(Session session, String login);
    void delete(Session session, Long id);
    long count(Session session);
}
