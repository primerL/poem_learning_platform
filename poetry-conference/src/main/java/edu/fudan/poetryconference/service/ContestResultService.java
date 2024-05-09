package edu.fudan.poetryconference.service;

import edu.fudan.poetryconference.model.ContestResult;
import edu.fudan.poetryconference.repository.ContestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface ContestResultService {

    ContestResult saveContestResult(ContestResult contestResult);

    // Other business methods can be added here

}