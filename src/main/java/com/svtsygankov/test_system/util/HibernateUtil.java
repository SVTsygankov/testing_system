package com.svtsygankov.test_system.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");

                // Явно добавляем классы (на всякий случай)
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.User.class);
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.Test.class);
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.Question.class);
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.Answer.class);
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.UserAnswer.class);
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.Result.class);

                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder()
                                .applySettings(configuration.getProperties());

                sessionFactory = configuration.buildSessionFactory(registryBuilder.build());
                log.warn("sessionFactory создана");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create sessionFactory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
