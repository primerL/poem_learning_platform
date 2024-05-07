package edu.fudan.poetryconference.service;

import edu.fudan.poetryconference.model.Question;

import java.util.Optional;

public interface QuestionService {
    Optional<Question> getQuestionById(int id);
}
