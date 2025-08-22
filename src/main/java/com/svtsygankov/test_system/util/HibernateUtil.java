package com.svtsygankov.test_system.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static SessionFactory sessionFactory;


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Явно указываем путь к конфигурационному файлу
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");

                // Явно добавляем классы (на всякий случай)
                configuration.addAnnotatedClass(com.svtsygankov.test_system.entity.User.class);

                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder()
                                .applySettings(configuration.getProperties());

                sessionFactory = configuration.buildSessionFactory(registryBuilder.build());

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
//    public static SessionFactory getSessionFactory() {
//        try {
//            Configuration configuration = new Configuration().configure(); // <-- Загружает hibernate.cfg.xml
//            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
//                    .applySettings(configuration.getProperties()).build();
//
//            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to create sessionFactory", e);
//        }
//        return sessionFactory;
//    }
}
