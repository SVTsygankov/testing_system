package com.svtsygankov.test_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"questions"})
@Entity
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 100)
    private String topic;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();


    // Удобные методы для работы со списком вопросов
    public void addQuestion(Question question) {
        questions.add(question);
        question.setTest(this);
    }

    @JsonIgnore
    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestions(List<Question> questions) {
        if (questions != null) {
            questions.forEach(this::addQuestion);
        }
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTest(null);
    }
}