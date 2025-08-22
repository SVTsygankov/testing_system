<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 19.06.2025
  Time: 9:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Регистрация</h2>

<c:if test="${not empty registrationErrorMessage}">
  <div class="error">${registrationErrorMessage}</div>
</c:if>

<form method="post">
  <input type="text" name="login" placeholder="Логин" required>
  <input type="password" name="password" placeholder="Пароль" required>
  <input type="password" name="confirmPassword" placeholder="Подтвердите пароль" required>
  <input type="submit" value="Зарегистрироваться">
</form>

<p style="text-align:center; margin-top: 15px;">
  <a href="/login">Уже есть аккаунт?</a>
</p>
