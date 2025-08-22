<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 11.08.2025
  Time: 14:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="ru_RU" scope="session"/>
<fmt:setBundle basename="messages"/>

<div class="result-details">
    <h2><fmt:message key="details.test_results"/></h2>

    <p class="centered">
        <strong><fmt:message key="details.test_id"/>:</strong> ${result.testId}
    </p>

    <p class="centered">
        <strong><fmt:message key="details.date"/>:</strong>
        <fmt:formatDate value="${resultDateAsDate}" pattern="dd.MM.yyyy HH:mm"/>
    </p>

    <c:if test="${empty result.answers}">
        <p class="centered"><em><fmt:message key="details.no_questions"/></em></p>
    </c:if>

    <c:forEach items="${result.answers}" var="answer" varStatus="status">
        <div class="question-block">
            <h4>${status.index + 1}. ${answer.askedQuestion}</h4>

            <p>
                <strong><fmt:message key="details.your_answer"/>:</strong>
                <span class="${answer.correct ? 'text-success' : 'text-danger'}">
                        ${answer.selectedAnswer}
                </span>
            </p>

            <c:choose>
                <c:when test="${answer.correct}">
                    <p class="text-success">
                        <fmt:message key="details.correct"/>
                    </p>
                </c:when>
                <c:otherwise>
                    <p class="text-danger">
                        <fmt:message key="details.wrong"/>
                    </p>
                    <p>
                        <strong><fmt:message key="details.correct_answer"/>:</strong>
                            ${answer.correctAnswer}
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
        <br/>
    </c:forEach>

<%--    <c:forEach items="${result.answers}" var="answer" varStatus="status">--%>
<%--        <div class="question-block">--%>
<%--            <h4>${status.index + 1}. ${answer.askedQuestion}</h4>--%>
<%--            <p>--%>
<%--                <strong><fmt:message key="details.your_answer"/>:</strong>--%>
<%--                <span class="${answer.correct ? 'text-success' : 'text-danger'}">--%>
<%--                        ${answer.selectedAnswer}--%>
<%--                </span>--%>
<%--            </p>--%>
<%--            <c:if test="${!answer.correct}">--%>
<%--                <p class="text-muted">--%>
<%--                    <fmt:message key="details.correct_answer_hint"/>--%>
<%--                </p>--%>
<%--            </c:if>--%>
<%--        </div>--%>
<%--        <br/> <!-- Пустая строка между вопросами -->--%>
<%--    </c:forEach>--%>

    <p class="centered">
        <a href="${pageContext.request.contextPath}/secure/history" class="btn btn-secondary">
            <fmt:message key="button.back_to_history"/>
        </a>
    </p>
</div>