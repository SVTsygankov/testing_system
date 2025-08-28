package com.svtsygankov.test_system.dao.impl;

import com.svtsygankov.test_system.dao.UserDao;
import com.svtsygankov.test_system.entity.User;
import com.svtsygankov.test_system.util.HibernateSessionManager;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class UserDaoImpl implements UserDao {

    @Override
    public User save(User user) {
        Session session = HibernateSessionManager.getCurrentSession();
        if (user.getId() == null) {
            session.persist(user);
        } else {
            session.merge(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        Session session = HibernateSessionManager.getCurrentSession();
        User user = session.get(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Session session = HibernateSessionManager.getCurrentSession();
        return session.createQuery("FROM User WHERE login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }

    @Override
    public List<User> findAll() {
        Session session = HibernateSessionManager.getCurrentSession();
        Query<User> query = session.createQuery("FROM User", User.class);
        return query.getResultList();
    }

    @Override
    public boolean existsByLogin(String login) {
        Session session = HibernateSessionManager.getCurrentSession();
        TypedQuery<Long> query = session.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.login = :login",
                Long.class);
        query.setParameter("login", login);
        return query.getSingleResult() > 0;
}

    @Override
    public void delete(Long id) {
        Session session = HibernateSessionManager.getCurrentSession();
        User user = session.get(User.class, id);
        if (user != null) {
            session.remove(user);
        }
    }

    @Override
        public long count() {
            Session session = HibernateSessionManager.getCurrentSession();
            return session.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                    .uniqueResultOptional()
                    .orElse(0L);
        }
}
