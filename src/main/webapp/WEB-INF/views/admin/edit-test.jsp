<%--
  Created by IntelliJ IDEA.
  User: Сергей
  Date: 28.06.2025
  Time: 13:18
  To change this template use File | Settings | File Templates.
--%>
<%-- /WEB-INF/views/admin/edit-test.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.svtsygankov.project_servlet_java_rush.entity.Test" %>

<%
  Test test = (Test) request.getAttribute("test");
  ObjectMapper objectMapper = (ObjectMapper) application.getAttribute("objectMapper");
  String testJson = objectMapper.writeValueAsString(test)
          .replace("'", "\\'")
          .replace("\"", "\\\"");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Редактировать тест</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    .question-block { border: 1px solid #ddd; padding: 15px; margin-bottom: 20px; }
    .answer-item { margin: 10px 0; padding: 10px; background: #f0f0f0; }
    textarea, input[type="text"] { width: 100%; padding: 8px; margin: 5px 0; }
    button { padding: 8px 15px; margin-right: 10px; cursor: pointer; }
    .delete-btn { background: #ff6b6b; color: white; border: none; }
    #error-container {
      color: red;
      margin: 20px 0;
      padding: 15px;
      border: 1px solid red;
      border-radius: 4px;
      display: none;
    }
    #loading-indicator {
      display: none;
      position: fixed;
      top: 20px;
      right: 20px;
      background: rgba(0,0,0,0.7);
      color: white;
      padding: 10px 15px;
      border-radius: 4px;
    }
  </style>
</head>
<body>
<h2>Редактировать тест</h2>

<div id="error-container">
  <h4>Ошибки валидации:</h4>
  <ul id="error-list"></ul>
</div>

<div id="loading-indicator">Сохранение...</div>

<form id="test-form">
  <input type="hidden" id="test-id" value="${test.id}">

  <div>
    <label>Название теста:</label>
    <input type="text" id="test-title" value="${fn:escapeXml(test.title)}" required>
  </div>

  <div>
    <label>Тема теста:</label>
    <input type="text" id="test-topic" value="${fn:escapeXml(test.topic)}" required>
  </div>

  <h3>Вопросы</h3>
  <div id="questions-container">
    <!-- Вопросы будут рендериться здесь -->
  </div>

  <button type="button" id="add-question-btn">+ Добавить вопрос</button>
  <button type="submit" id="submit-btn">Сохранить</button>
</form>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const testState = JSON.parse('<%= testJson %>');
    console.log("Initial test state:", testState);

    // Функции для работы с ошибками
    function showErrors(errors) {
      const errorContainer = document.getElementById('error-container');
      const errorList = document.getElementById('error-list');

      errorList.innerHTML = '';
      errors.forEach(error => {
        const li = document.createElement('li');
        li.textContent = error;
        errorList.appendChild(li);
      });

      errorContainer.style.display = 'block';
      window.scrollTo(0, 0); // Прокрутка к ошибкам
    }

    function hideErrors() {
      document.getElementById('error-container').style.display = 'none';
    }

    // Функция рендеринга вопросов и ответов
    function renderQuestions() {
      const container = document.getElementById('questions-container');
      container.innerHTML = '';

      if (!testState.questions || testState.questions.length === 0) {
        container.innerHTML = '<p>Пока нет вопросов. Добавьте первый вопрос.</p>';
        return;
      }

      testState.questions.forEach((question, qIndex) => {
        const questionDiv = document.createElement('div');
        questionDiv.className = 'question-block';
        questionDiv.dataset.index = qIndex;

        // Поле вопроса
        const textarea = document.createElement('textarea');
        textarea.rows = 3;
        textarea.value = question.text || '';
        textarea.placeholder = "Введите текст вопроса";
        textarea.required = true;
        textarea.addEventListener('input', (e) => {
          question.text = e.target.value;
        });

        // Кнопка удаления вопроса
        const deleteQuestionBtn = document.createElement('button');
        deleteQuestionBtn.className = 'delete-btn';
        deleteQuestionBtn.textContent = 'Удалить вопрос';
        deleteQuestionBtn.addEventListener('click', () => {
          if (testState.questions.length <= 1) {
            alert("Тест должен содержать хотя бы один вопрос!");
            return;
          }
          testState.questions.splice(qIndex, 1);
          renderQuestions();
        });

        // Контейнер для ответов
        const answerContainer = document.createElement('div');
        answerContainer.className = 'answer-block';

        // Кнопка добавления ответа
        const addAnswerBtn = document.createElement('button');
        addAnswerBtn.textContent = '+ Добавить ответ';
        addAnswerBtn.addEventListener('click', () => {
          if (!question.answers) question.answers = [];
          question.answers.push({ text: "", correct: false });
          renderQuestions();
        });

        // Рендеринг ответов
        if (question.answers && question.answers.length > 0) {
          question.answers.forEach((answer, aIndex) => {
            const answerDiv = document.createElement('div');
            answerDiv.className = 'answer-item';

            // Поле ответа
            const answerInput = document.createElement('input');
            answerInput.type = 'text';
            answerInput.value = answer.text || '';
            answerInput.placeholder = "Введите текст ответа";
            answerInput.required = true;
            answerInput.addEventListener('input', (e) => {
              answer.text = e.target.value;
            });

            // Чекбокс "Правильный ответ"
            const checkboxLabel = document.createElement('label');
            checkboxLabel.style.marginLeft = '10px';

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = answer.correct || false;
            checkbox.addEventListener('change', (e) => {
              answer.correct = e.target.checked;
            });

            checkboxLabel.appendChild(checkbox);
            checkboxLabel.appendChild(document.createTextNode(' Правильный ответ'));

            // Кнопка удаления ответа
            const deleteAnswerBtn = document.createElement('button');
            deleteAnswerBtn.className = 'delete-btn';
            deleteAnswerBtn.textContent = 'Удалить';
            deleteAnswerBtn.addEventListener('click', () => {
              if (question.answers.length <= 1) {
                alert("Вопрос должен содержать хотя бы один ответ!");
                return;
              }
              question.answers.splice(aIndex, 1);
              renderQuestions();
            });

            answerDiv.appendChild(answerInput);
            answerDiv.appendChild(checkboxLabel);
            answerDiv.appendChild(deleteAnswerBtn);
            answerContainer.appendChild(answerDiv);
          });
        }

        questionDiv.appendChild(textarea);
        questionDiv.appendChild(answerContainer);
        questionDiv.appendChild(addAnswerBtn);
        questionDiv.appendChild(deleteQuestionBtn);
        container.appendChild(questionDiv);
      });
    }

    // Обработчик добавления нового вопроса
    document.getElementById('add-question-btn').addEventListener('click', () => {
      if (!testState.questions) testState.questions = [];
      testState.questions.push({ text: "", answers: [] });
      renderQuestions();
    });

    // Обработчик отправки формы
    document.getElementById('test-form').addEventListener('submit', async (e) => {
      e.preventDefault();

      // Обновляем данные перед отправкой
      testState.title = document.getElementById('test-title').value;
      testState.topic = document.getElementById('test-topic').value;

      // Скрываем предыдущие ошибки
      hideErrors();

      // Показываем индикатор загрузки
      const loadingIndicator = document.getElementById('loading-indicator');
      const submitBtn = document.getElementById('submit-btn');
      loadingIndicator.style.display = 'block';
      submitBtn.disabled = true;

      try {
        const response = await fetch('/admin/test/edit', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(testState)
        });

        const data = await response.json();

        if (response.ok) {
          if (data.success && data.redirectUrl) {
            window.location.href = data.redirectUrl;
          }
        } else {
          if (data.errors) {
            showErrors(data.errors);
          } else {
            throw new Error('Неизвестная ошибка сервера');
          }
        }
      } catch (error) {
        showErrors([error.message]);
        console.error('Ошибка:', error);
      } finally {
        loadingIndicator.style.display = 'none';
        submitBtn.disabled = false;
      }
    });

    // Первоначальный рендеринг
    renderQuestions();
  });
</script>
</body>
</html>