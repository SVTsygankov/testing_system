package com.svtsygankov.test_system.entity;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"answers"})
@Entity
@Table(name = "test_results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "date", nullable = false)
    private LocalDateTime date; // Hibernate 5.6+ поддерживает JSR-310 (LocalDateTime) напрямую

    // Связь OneToMany с UserAnswer
    // orphanRemoval = true: если UserAnswer удаляется из списка, он автоматически удаляется из БД
    // cascade = CascadeType.ALL: операции (persist, merge, remove) каскадируются на UserAnswer
    @Builder.Default
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserAnswer> answers = new ArrayList<>();


    // Двухсторонняя связь между Result и List<UserAnswer> answers
    public void addAnswer(UserAnswer answer) {
        if (answer != null && !this.answers.contains(answer)) {
            this.answers.add(answer);
            answer.setResult(this);
        }
    }

    public void removeAnswer(UserAnswer answer) {
        if (answer != null && this.answers.remove(answer)) {
            answer.setResult(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result result)) return false;
        return id != null && id.equals(result.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}