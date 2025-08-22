<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 14.08.2025
  Time: 14:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/passing-test.css">

<div class="passing-test-container">
  <h1 class="test-title">Тест: ${test.title}</h1>

  <form action="${pageContext.request.contextPath}/submit-test" method="post">
    <input type="hidden" name="testId" value="${test.id}">

    <c:forEach items="${test.questions}" var="question" varStatus="qLoop">
      <div class="question">
        <div class="question-text">
          Вопрос ${qLoop.index + 1}: ${question.text}
        </div>

        <c:forEach items="${question.answers}" var="answer" varStatus="aLoop">
          <div class="answer-option">
            <input type="radio"
                   id="q_${qLoop.index}_a_${aLoop.index}"
                   name="question_${qLoop.index}"
                   value="${aLoop.index}"
                   required>
            <label for="q_${qLoop.index}_a_${aLoop.index}">
                ${answer.text}
            </label>
          </div>
        </c:forEach>
      </div>
    </c:forEach>

    <button type="submit" class="submit-btn">Завершить тест</button>
  </form>
</div>
