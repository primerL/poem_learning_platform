package edu.fudan.poem_service.service.impl;

import edu.fudan.poem_service.entity.Answers;
import edu.fudan.poem_service.repository.AnswersRepository;
import edu.fudan.poem_service.service.AnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswersServiceImpl implements AnswersService {
    @Autowired
    private AnswersRepository answersRepository;

    @Override
    public boolean saveAnswers(Answers answers) {
        if(!answers.getCorrect()) {
            answersRepository.save(answers);
        }
        return true;
    }
}