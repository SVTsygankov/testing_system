<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 01.08.2025
  Time: 11:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setLocale value="ru_RU" scope="session"/>
<fmt:setBundle basename="messages"/>

<div class="history-content">
  <h2 class="history-title"><fmt:message key="history.your_results"/></h2>

  <c:if test="${empty resultsWithCounts}">
    <p class="empty-message"><fmt:message key="history.no_results"/></p>
  </c:if>
  <c:forEach items="${resultsWithCounts}" var="resultData">
    <h3>
      <fmt:message key="history.test_number"/> ${resultData.result.testId} -
      <fmt:formatDate value="${resultData.dateAsDate}" pattern="dd.MM.yyyy HH:mm"/>
    </h3>
    <p>
      <fmt:message key="history.correct_answers"/>:
        ${resultData.correctAnswersCount} / ${fn:length(resultData.result.answers)}
      (${resultData.successRate}%)
    </p>
<%--    <a href="...">--%>
<%--      <fmt:message key="history.view_details"/>--%>
<%--    </a>--%>
    <a href="${pageContext.request.contextPath}/secure/result-details?id=${resultData.result.id}">
      <fmt:message key="history.view_details"/>
    </a>
  </c:forEach>

</div>
