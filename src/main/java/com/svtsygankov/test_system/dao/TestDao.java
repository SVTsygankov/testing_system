package com.svtsygankov.test_system.dao;

import com.svtsygankov.test_system.entity.Test;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface TestDao {
    Test save(Test test);
    Optional<Test> findById(Integer id);
    List<Test> findAll();

//    List<Test> findByTopic(Session session, String topic);
//    List<Test> findByCreatedBy(Session session, Long userId);

    void delete(Integer id);
    long count();
}