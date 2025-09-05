package com.svtsygankov.test_system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"questions"})
@Entity
@Table(name = "result_answers") // Имя таблицы в БД
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Используем TEXT для потенциально длинных строк
    @Column(name = "asked_question", columnDefinition = "TEXT", nullable = false)
    private String askedQuestion;

    @Column(name = "selected_answer", columnDefinition = "TEXT", nullable = false)
    private String selectedAnswer;

    @Column(name = "correct", nullable = false)
    private boolean correct;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer; // Может быть null, если не нужно

    // Связь ManyToOne с Result
    @ManyToOne(fetch = FetchType.LAZY) // Ленивая загрузка
    @JoinColumn(name = "result_id", nullable = false, foreignKey = @ForeignKey(name = "fk_useranswer_result"))
    private Result result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAnswer that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
