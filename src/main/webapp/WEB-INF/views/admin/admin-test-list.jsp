<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 02.07.2025
  Time: 12:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Управление тестами</h2>

<div style="margin-bottom: 15px;">
  <a href="/admin/test/create" class="btn btn-primary">+ Создать тест</a>
</div>

<c:if test="${empty testsByTopic}">
  <div class="alert alert-warning">Нет доступных тестов.</div>
</c:if>

<c:if test="${not empty testsByTopic}">
  <c:forEach items="${testsByTopic}" var="entry">
    <h3>${entry.key}</h3>
    <div class="list-group">
      <c:forEach items="${entry.value}" var="test">
        <div class="list-group-item d-flex justify-content-between align-items-center">
          <a href="/take-test?id=${test.id}">${test.title}</a>
          <div>
            <a href="/admin/test/edit?id=${test.id}" class="btn btn-sm btn-outline-primary">Редактировать</a>
            <button onclick="showDeleteModal(${test.id}, '${test.title}')"
                    class="btn btn-sm btn-outline-danger">
              Удалить
            </button>
            <a href="/admin/test/stats?id=${test.id}" class="btn btn-sm btn-outline-info">Статистика</a>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:forEach>
  <br>
  <a href="/admin/statistics" class="btn btn-secondary">
    <i class="fas fa-chart-bar"></i> Статистика
  </a>
</c:if>

<!-- Модальное окно (добавляем в конец файла) -->
<div id="deleteModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:1000;">
  <div style="background:white; width:400px; margin:100px auto; padding:20px; border-radius:5px;">
    <h3>Подтверждение удаления</h3>
    <p>Вы уверены, что хотите удалить тест "<span id="testTitle"></span>"?</p>
    <div style="text-align:right; margin-top:20px;">
      <button onclick="confirmDelete()" class="btn btn-danger">Удалить</button>
      <button onclick="hideModal()" class="btn btn-secondary" style="margin-left:10px;">Отмена</button>
    </div>
  </div>
</div>

<script>
  let currentTestId = 0;

  function showDeleteModal(testId, testTitle) {
    currentTestId = testId;
    document.getElementById('testTitle').textContent = testTitle;
    document.getElementById('deleteModal').style.display = 'block';
  }

  function hideModal() {
    document.getElementById('deleteModal').style.display = 'none';
  }

  function confirmDelete() {
    fetch('/admin/test/delete?id=' + currentTestId, {
      method: 'POST'
    })
            .then(response => {
              if (response.ok) {
                window.location.reload();
              } else {
                alert('Ошибка при удалении');
              }
            })
            .catch(error => {
              console.error('Error:', error);
              alert('Ошибка при удалении');
            });
  }
</script>