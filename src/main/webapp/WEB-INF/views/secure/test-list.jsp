<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 30.06.2025
  Time: 14:40
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Доступные тесты</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/test-list.css">
</head>
<body class="test-list-container">
<div class="test-list-wrapper">
  <h1 class="test-list-header">Доступные тесты</h1>

  <div class="spacer"></div>

  <c:if test="${empty testsByTopic}">
    <div class="no-tests-message">
      <p>Нет доступных тестов.</p>
    </div>
  </c:if>

  <c:if test="${not empty testsByTopic}">
    <table class="tests-table">
      <thead>
      <tr>
        <th>Название теста</th>
        <th>Действия</th>
      </tr>
      </thead>
    </table>

    <div class="test-table-container">
      <table class="tests-table">
        <tbody>
        <c:forEach items="${testsByTopic}" var="topicGroup">
          <tr>
            <td colspan="2" class="test-topic-header">Тема: ${topicGroup.key}</td>
          </tr>
          <c:forEach items="${topicGroup.value}" var="test">
            <tr>
              <td>${test.title}</td>
              <td>
                <a href="${pageContext.request.contextPath}/start-test?id=${test.id}"
                   class="take-test-btn">
                  Пройти тест
                </a>
              </td>
            </tr>
          </c:forEach>
          <tr><td colspan="2"><div class="spacer"></div></td></tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </c:if>

  <div class="history-link-container">
    <a href="${pageContext.request.contextPath}/secure/history" class="history-link">
      Просмотр истории тестирований
    </a>
  </div>
</div>
</body>
</html>