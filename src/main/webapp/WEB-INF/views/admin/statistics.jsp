<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 14.08.2025
  Time: 11:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="ru_RU"/>
<fmt:setBundle basename="messages"/>

<div class="statistics-container">
    <h2><fmt:message key="admin.statistics.title"/></h2>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">
                <i class="fas fa-users"></i>
            </div>
            <div class="stat-content">
                <h3><fmt:message key="admin.statistics.users"/></h3>
                <p class="stat-number">${userCount}</p>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon">
                <i class="fas fa-file-alt"></i>
            </div>
            <div class="stat-content">
                <h3><fmt:message key="admin.statistics.tests"/></h3>
                <p class="stat-number">${testCount}</p>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon">
                <i class="fas fa-clipboard-check"></i>
            </div>
            <div class="stat-content">
                <h3><fmt:message key="admin.statistics.results"/></h3>
                <p class="stat-number">${resultCount}</p>
            </div>
        </div>
    </div>
</div>
