<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 11.08.2025
  Time: 14:15
  To change this template use File | Settings | File Templates.
--%>
<%-- /WEB-INF/views/secure/result-details-content.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="ru_RU" scope="session"/>
<fmt:setBundle basename="messages"/>

<div class="result-details">
    <h2><fmt:message key="details.test_results"/></h2>

    <div class="test-summary">
        <p><strong><fmt:message key="details.test_results"/>:</strong> ${result.testTitle}</p>
        <p><strong><fmt:message key="details.test_id"/>:</strong> ${result.testId}</p>
        <p><strong><fmt:message key="details.date"/>:</strong>
            <%-- Используем result.date из DTO --%>
            <fmt:formatDate value="${result.date}" pattern="dd.MM.yyyy HH:mm"/>
        </p>
        <p><strong><fmt:message key="details.score"/>:</strong>
            ${result.correctCount} / ${result.totalCount}
            <c:if test="${result.totalCount > 0}">
                (${Math.round(result.correctCount * 100.0 / result.totalCount)}%)
            </c:if>
        </p>
    </div>

    <c:if test="${empty result.answers}">
        <p class="centered"><em><fmt:message key="details.no_questions"/></em></p>
    </c:if>

    <div class="questions-container">
        <c:forEach items="${result.answers}" var="answer" varStatus="status">
            <div class="question-block ${answer.correct ? 'correct' : 'incorrect'}">
                <div class="question-header">
                    <h4>${status.index + 1}. ${answer.askedQuestion}</h4>
                    <span class="question-status ${answer.correct ? 'status-correct' : 'status-incorrect'}">
                            ${answer.correct ? '✓ Верно' : '✗ Неверно'}
                    </span>
                </div>

                <div class="answer-section">
                    <p>
                        <strong><fmt:message key="details.your_answer"/>:</strong>
                        <span class="${answer.correct ? 'text-success' : 'text-danger'}">
                                ${answer.selectedAnswer}
                        </span>
                    </p>

                    <c:if test="${not answer.correct and not empty answer.correctAnswer}">
                        <p>
                            <strong><fmt:message key="details.correct_answer"/>:</strong>
                            <span class="text-success">${answer.correctAnswer}</span>
                        </p>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </div>

    <div class="actions centered">
        <a href="${pageContext.request.contextPath}/secure/history" class="btn btn-secondary">
            <fmt:message key="button.back_to_history"/>
        </a>
        <a href="${pageContext.request.contextPath}/secure/tests" class="btn btn-primary">
            <fmt:message key="button.back_to_history"/>
        </a>
    </div>
</div>