package com.svtsygankov.test_system.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateSessionManager {
    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();
    private static SessionFactory sessionFactory;

    public static void setSessionFactory(SessionFactory factory) {
        sessionFactory = factory;
    }

    public static Session getCurrentSession() {
        Session session = sessionThreadLocal.get();
        if (session == null || !session.isOpen()) {
            session = sessionFactory.getCurrentSession();
            sessionThreadLocal.set(session);
        }
        return session;
    }

    public static void closeSession() {
        Session session = sessionThreadLocal.get();
        if (session != null && session.isOpen()) {
            session.close();
        }
        sessionThreadLocal.remove();
    }
}