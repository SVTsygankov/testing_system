<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 01.08.2025
  Time: 11:30
  To change this template use File | Settings | File Templates.
--%>
<%-- WEB-INF/views/secure/history-content.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="ru_RU" scope="session"/>
<fmt:setBundle basename="messages"/>

<div class="history-content">
  <h2 class="history-title"><fmt:message key="history.your_results"/></h2>

  <c:if test="${empty results}">
    <p class="empty-message"><fmt:message key="history.no_results"/></p>
  </c:if>

  <c:forEach items="${results}" var="resultDto">
    <div class="result-item card mb-3">
      <div class="card-body">
        <h5 class="card-title">
          <fmt:message key="history.test_number"/> ${resultDto.testId} - ${resultDto.testTitle}
        </h5>
        <p class="card-text">
          <small class="text-muted">
            <fmt:message key="history.date"/>:
            <fmt:formatDate value="${resultDto.date}" pattern="dd.MM.yyyy HH:mm"/>
          </small>
        </p>

        <p class="card-text">
          <fmt:message key="history.correct_answers"/>:
          <strong>${resultDto.correctCount} / ${resultDto.totalCount}</strong>
          <c:if test="${resultDto.totalCount > 0}">
            (${Math.round(resultDto.correctCount * 100.0 / resultDto.totalCount)}%)
          </c:if>
        </p>

        <a href="${pageContext.request.contextPath}/secure/result-details?id=${resultDto.id}"
           class="btn btn-primary">
          <fmt:message key="history.view_details"/>
        </a>
      </div>
    </div>
  </c:forEach>
</div>