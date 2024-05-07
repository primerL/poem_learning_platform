package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.Question;
import edu.fudan.poetryconference.repository.QuestionRepository;
import edu.fudan.poetryconference.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public Optional<Question> getQuestionById(int id) {
        return questionRepository.findById(id);
    }
}
