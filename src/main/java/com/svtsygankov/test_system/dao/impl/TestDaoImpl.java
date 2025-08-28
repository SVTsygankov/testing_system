package com.svtsygankov.test_system.dao.impl;

import com.svtsygankov.test_system.dao.TestDao;
import com.svtsygankov.test_system.entity.Test;
import com.svtsygankov.test_system.util.HibernateSessionManager;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class TestDaoImpl implements TestDao {

    @Override
    public Test save(Test test) {
        Session session = HibernateSessionManager.getCurrentSession();
        if (test.getId() == null) {
            session.persist(test);
        } else {
            session.merge(test);
        }
        return test;
    }

    @Override
    public Optional<Test> findById(Integer id) {
        Session session = HibernateSessionManager.getCurrentSession();
        Test test = session.get(Test.class, id);
        return Optional.ofNullable(test);
    }

    @Override
    public List<Test> findAll() {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<Test> query = session.createQuery("FROM Test", Test.class);
        return query.getResultList();
    }

    @Override
    public void delete(Integer id) {
        Session session = HibernateSessionManager.getCurrentSession();
        Test test = session.get(Test.class, id);
        if (test != null) {
            session.remove(test);
        }
    }

    @Override
    public long count() {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(t) FROM Test t", Long.class);
        return query.uniqueResult();
    }
}