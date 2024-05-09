package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.ContestResult;
import edu.fudan.poetryconference.repository.ContestResultRepository;
import edu.fudan.poetryconference.service.ContestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestResultServiceImpl implements ContestResultService {
    private final ContestResultRepository contestResultRepository;

    @Autowired
    public ContestResultServiceImpl(ContestResultRepository contestResultRepository) {
        this.contestResultRepository = contestResultRepository;
    }

    @Override
    public ContestResult saveContestResult(ContestResult contestResult) {
        return contestResultRepository.save(contestResult);
    }

    // 实现其他业务逻辑方法
}
