package edu.fudan.poetryconference.service;

import edu.fudan.poetryconference.dto.UserContestNumDTO;
import edu.fudan.poetryconference.dto.UserContestResultDTO;
import edu.fudan.poetryconference.dto.UserWinRateDTO;
import edu.fudan.poetryconference.model.ContestResult;

import java.util.List;

public interface ContestResultService {
    boolean saveContestResult(ContestResult contestResult);

    List<UserContestResultDTO> getUserContestResults(Long userId);

    List<Double> getUserContestWinRate(Long userId);

    Double getUserWinRateTotal(Long userId);

    List<Integer> getUserContestNum(Long userId);
}
