<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 19.06.2025
  Time: 9:50
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="ru_RU"/>
<fmt:setBundle basename="messages"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title><c:out value="${pageTitle}" default="Приложение"/></title>

  <!-- Подключаем стили -->
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">

  <!-- Подключаем Font Awesome для иконок -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>

<header>
  <div class="header-left">
    <c:if test="${not pageContext.request.requestURI.endsWith('/login') &&
                  not pageContext.request.requestURI.endsWith('/register')}">
      <a href="javascript:history.back()" class="btn btn-back">
        <i class="fas fa-arrow-left"></i> <fmt:message key="button.back" />
      </a>
    </c:if>
  </div>

  <h1><fmt:message key="header.app.title" /></h1>

  <div class="mode-switcher">
    <c:choose>
      <%-- Админ в админ-панели: кнопка "Перейти в пользовательский режим" --%>
      <c:when test="${requestScope['jakarta.servlet.forward.request_uri'].startsWith('/admin/')}">
        <a href="/secure/switch-to-user" class="btn btn-user-mode">
          <i class="fas fa-user"></i> <fmt:message key="header.button.user.mode" />
        </a>
      </c:when>

      <%-- Админ в пользовательской части: кнопка "Админ-панель" --%>
      <c:when test="${sessionScope.user.role == 'ADMIN' &&
                            !requestScope['jakarta.servlet.forward.request_uri'].startsWith('/admin/')}">
        <a href="/secure/switch-to-admin" class="btn btn-admin">
          <i class="fas fa-cog"></i> <fmt:message key="header.button.admin.panel" />
        </a>
      </c:when>
    </c:choose>

    <%-- Кнопка выхода — только если пользователь авторизован --%>
    <c:if test="${sessionScope.user != null}">
      <a href="/secure/logout" class="btn btn-logout">
        <i class="fas fa-sign-out-alt"></i> <fmt:message key="header.button.logout" />
      </a>
    </c:if>
  </div>
</header>

<div class="container">
  <div class="form-container">
    <jsp:include page="${contentPage}" />
  </div>
</div>

<footer>
  &copy; 2025 — Веб-приложение для создания и прохождения тестов
</footer>
</body>
</html>