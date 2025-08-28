package com.svtsygankov.test_system.dao;

import com.svtsygankov.test_system.entity.User;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    List<User> findAll();
    boolean existsByLogin(String login);
    void delete(Long id);
    long count();
}
