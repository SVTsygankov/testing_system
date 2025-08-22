<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 27.07.2025
  Time: 12:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/layout.jsp">
  <jsp:param name="pageTitle" value="Результаты теста"/>
  <jsp:param name="contentPage" value="/WEB-INF/views/secure/test-result-content.jsp"/>
</jsp:include>
