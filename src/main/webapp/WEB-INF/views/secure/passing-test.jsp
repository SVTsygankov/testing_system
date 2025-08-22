<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 25.07.2025
  Time: 13:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/layout.jsp">
  <jsp:param name="pageTitle" value="Прохождение теста: ${test.title}"/>
  <jsp:param name="contentPage" value="/WEB-INF/views/secure/passing-test-content.jsp"/>
</jsp:include>
