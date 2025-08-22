package com.svtsygankov.test_system.dao.impl;

import com.svtsygankov.test_system.dao.UserDao;
import com.svtsygankov.test_system.entity.User;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class UserDaoImpl implements UserDao {

    @Override
    public User save(Session session, User user) {
        if (user.getId() == null) {
            session.persist(user);
        } else {
            session.merge(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Session session, Long id) {
        User user = session.get(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByLogin(Session session, String login) {
        return session.createQuery("FROM User WHERE login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }

    @Override
    public List<User> findAll(Session session) {
        Query<User> query = session.createQuery("FROM User", User.class);
        return query.getResultList();
    }

    @Override
    public boolean existsByLogin(Session session, String login) {
        TypedQuery<Long> query = session.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.login = :login",
                Long.class);
        query.setParameter("login", login);
        return query.getSingleResult() > 0;
}

    @Override
    public void delete(Session session, Long id) {
        User user = session.get(User.class, id);
        if (user != null) {
            session.remove(user);
        }
    }

    @Override
        public long count(Session session) {
            return session.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                    .uniqueResultOptional()
                    .orElse(0L);
        }
}
