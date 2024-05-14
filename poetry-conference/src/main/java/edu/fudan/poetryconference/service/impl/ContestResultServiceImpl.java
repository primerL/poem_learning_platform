package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.ContestResult;
import edu.fudan.poetryconference.repository.ContestResultRepository;
import edu.fudan.poetryconference.service.ContestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestResultServiceImpl implements ContestResultService {
    @Autowired
    private ContestResultRepository contestResultRepository;

    @Override
    public boolean saveContestResult(ContestResult contestResult) {
        contestResultRepository.save(contestResult);
        return true;
    }
}
