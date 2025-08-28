package com.svtsygankov.test_system.filter;

import com.svtsygankov.test_system.util.HibernateSessionManager;
import com.svtsygankov.test_system.util.HibernateUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;

@WebFilter("/*")
public class HibernateSessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализируем SessionFactory
        HibernateSessionManager.setSessionFactory(HibernateUtil.getSessionFactory());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Session session = null;
        Transaction transaction = null;

        try {
            // Открываем сессию
            session = HibernateSessionManager.getCurrentSession();
            transaction = session.beginTransaction();

            // Продолжаем обработку запроса
            chain.doFilter(request, response);

            // Коммитим транзакцию
            if (transaction != null && transaction.isActive()) {
                transaction.commit();
            }

        } catch (Exception e) {
            // Откатываем транзакцию при ошибке
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw new ServletException("Transaction rollback failed", e);

        } finally {
            // Закрываем сессию
            HibernateSessionManager.closeSession();
        }
    }

    @Override
    public void destroy() {
        // Очищаем ресурсы при завершении
        HibernateSessionManager.closeSession();
    }
}