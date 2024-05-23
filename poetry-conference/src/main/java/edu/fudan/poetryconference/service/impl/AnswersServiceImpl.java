package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.Answers;
import edu.fudan.poetryconference.repository.AnswersRepository;
import edu.fudan.poetryconference.service.AnswersService;
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
