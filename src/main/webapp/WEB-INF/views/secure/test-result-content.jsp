<%@ page import="com.svtsygankov.test_system.dto.ResultDto" %><%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 14.08.2025
  Time: 14:52
  To change this template use File | Settings | File Templates.
--%>
<%-- /WEB-INF/views/secure/test-result-content.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
  // Правильно получаем ResultDto
  ResultDto resultDto = (ResultDto) request.getAttribute("result");

  // Вычисляем процент правильных ответов
  int correctCount = resultDto.getCorrectCount();
  int totalCount = resultDto.getTotalCount();
  int percentage = totalCount > 0 ? Math.round((float) correctCount / totalCount * 100) : 0;

  // Определяем класс для цветовой индикации
  String resultClass;
  String scoreSummaryClass;
  if (percentage >= 80) {
    resultClass = "result-success";
    scoreSummaryClass = "success";
  } else if (percentage >= 50) {
    resultClass = "result-warning";
    scoreSummaryClass = "warning";
  } else {
    resultClass = "result-failed";
    scoreSummaryClass = "failed";
  }

  // Передаём в JSP
  request.setAttribute("percentage", percentage);
  request.setAttribute("resultClass", resultClass);
  request.setAttribute("scoreSummaryClass", scoreSummaryClass);
%>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/test-result.css">

<div class="test-result-container <%= resultClass %>">
  <h1 class="page-title">Результаты тестирования</h1>

  <div class="test-info">
    <p><strong>Тест:</strong> ${result.testTitle} (ID: ${result.testId})</p>
    <p><strong>Дата прохождения:</strong>
      <fmt:formatDate value="${result.date}" pattern="dd.MM.yyyy HH:mm"/>
    </p>
  </div>

  <div class="score-summary <%= scoreSummaryClass %>">
    <div class="score-number">
      ${result.correctCount} / ${result.totalCount}
    </div>
    <div class="score-label">Правильных ответов</div>
    <div class="percentage-display">
      <%= percentage %>%
      <c:choose>
        <c:when test="${percentage >= 80}">
          <span style="color: #28a745;">★ Отлично!</span>
        </c:when>
        <c:when test="${percentage >= 50}">
          <span style="color: #ffc107;">⚠️ Удовлетворительно</span>
        </c:when>
        <c:otherwise>
          <span style="color: #dc3545;">✗ Неудовлетворительно</span>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="answers-list">
    <c:forEach items="${result.answers}" var="answer" varStatus="loop">
      <div class="answer-item ${answer.correct ? 'correct' : 'incorrect'}">
        <div class="question-header">
          <h3>Вопрос ${loop.index + 1}</h3>
          <span class="status-badge ${answer.correct ? 'status-correct' : 'status-incorrect'}">
              ${answer.correct ? 'Верно ✓' : 'Неверно ✗'}
          </span>
        </div>
        <div class="question-text">${answer.askedQuestion}</div>
        <div class="your-answer">
          <strong>Ваш ответ:</strong> ${answer.selectedAnswer}
        </div>
        <c:if test="${not empty answer.correctAnswer and not answer.correct}">
          <div class="correct-answer">
            <strong>Правильный ответ:</strong> ${answer.correctAnswer}
          </div>
        </c:if>
      </div>
    </c:forEach>
  </div>

  <div class="actions">
    <a href="${pageContext.request.contextPath}/secure/tests" class="btn btn-primary">
      Вернуться к списку тестов
    </a>
    <a href="${pageContext.request.contextPath}/secure/history" class="btn btn-secondary">
      Просмотреть историю
    </a>
  </div>
</div>