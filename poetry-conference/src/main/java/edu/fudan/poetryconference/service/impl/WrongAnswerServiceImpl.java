package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.WrongAnswer;
import edu.fudan.poetryconference.repository.WrongAnswerRepository;
import edu.fudan.poetryconference.service.WrongAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WrongAnswerServiceImpl implements WrongAnswerService {
    private final WrongAnswerRepository wrongAnswerRepository;

    @Autowired
    public WrongAnswerServiceImpl(WrongAnswerRepository wrongAnswerRepository) {
        this.wrongAnswerRepository = wrongAnswerRepository;
    }

    @Override
    public WrongAnswer saveWrongAnswer(WrongAnswer wrongAnswer) {
        return wrongAnswerRepository.save(wrongAnswer);
    }
}
