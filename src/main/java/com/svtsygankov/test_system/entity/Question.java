package com.svtsygankov.test_system.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString(exclude = {"answers"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    public Question(String text) {
        this.text = text;
    }

    @JsonIgnore
    public Test getTest() { return test;}

    @JsonIgnore
    public List<Answer> getAnswers() { return answers; }

    public void addAnswer(Answer answer) {
        if (answer != null && !answers.contains(answer)) {
            answers.add(answer);
            answer.setQuestion(this);
        }
    }

    public void removeAnswer(Answer answer) {
        if (answer != null && this.answers.contains(answer)) {
            this.answers.remove(answer);
            answer.setQuestion(null); // ← ВАЖНО: разрываем связь с обеих сторон
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return id != null && id.equals(question.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}