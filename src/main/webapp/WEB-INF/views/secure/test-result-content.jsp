<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 14.08.2025
  Time: 14:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/test-result.css">

<div class="test-result-container">
  <h1 class="page-title">Результаты тестирования</h1>

  <div class="test-info">
    <p><strong>Тест ID:</strong> ${result.testId}</p>
    <p><strong>Дата прохождения:</strong>
      <fmt:formatDate value="${resultDateAsDate}" pattern="dd.MM.yyyy HH:mm"/>
    </p>
  </div>

  <div class="score-summary">
    <div class="score-number">
      ${result.answers.stream().filter(a -> a.correct).count()} / ${result.answers.size()}
    </div>
    <div class="score-label">Правильных ответов</div>
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
