package edu.fudan.poem_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Answers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long questionId;

    private Boolean correct;

    private LocalDateTime answerDate;

    private Long reviewTimes;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public Long getReviewTimes() {
        return reviewTimes;
    }


    public void setReviewTimes(Long reviewTimes) {
        this.reviewTimes = reviewTimes;
    }

    public Answers(Long userId, Long questionId, Boolean correct) {
        this.userId = userId;
        this.questionId = questionId;
        this.correct = correct;
        this.answerDate = LocalDateTime.now();
        this.reviewTimes = 0L;
    }

    public Answers() {}
}