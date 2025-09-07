package com.svtsygankov.test_system.dao.impl;

import com.svtsygankov.test_system.dao.ResultDao;
import com.svtsygankov.test_system.entity.Result;
import com.svtsygankov.test_system.util.HibernateSessionManager;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ResultDaoImpl implements ResultDao {

    @Override
    public Result save(Result result) {
        Session session = HibernateSessionManager.getCurrentSession();
        if (result.getId() == null) {
            session.persist(result);
        } else {
            session.merge(result);
        }
        return result;
    }

    @Override
    public Optional<Result> findById(Long id) {
        Session session = HibernateSessionManager.getCurrentSession();
        Result result = session.get(Result.class, id);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Result> findByUserId(Long userId) {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<Result> query = session.createQuery("""
                        SELECT DISTINCT r FROM Result r
                        LEFT JOIN FETCH r.answers
                        LEFT JOIN FETCH r.test
                        WHERE r.user.id = :userId
                        ORDER BY r.date DESC
                        """,Result.class);

        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Result> findByTestId(Integer testId) {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<Result> query = session.createQuery(
                "FROM Result r WHERE r.test.id = :testId ORDER BY r.date DESC", Result.class);
        query.setParameter("testId", testId);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Session session = HibernateSessionManager.getCurrentSession();
        Result result = session.get(Result.class, id);
        if (result != null) {
            session.remove(result);
        }
    }

    @Override
    public long count() {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(r) FROM Result r", Long.class);
        return query.uniqueResult();
    }

    @Override
    public List<Result> findAll() {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<Result> query = session.createQuery("FROM Result r ORDER BY r.date DESC", Result.class);
        return query.getResultList();
    }
}