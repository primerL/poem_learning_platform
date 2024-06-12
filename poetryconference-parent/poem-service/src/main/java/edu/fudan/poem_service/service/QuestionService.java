package edu.fudan.poem_service.service;

public interface QuestionService {
    String getQuestion();

    String getReviewQuestion(Long userId);
}

