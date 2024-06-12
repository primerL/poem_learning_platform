package edu.fudan.poem_service.service;

import edu.fudan.poem_service.entity.ContestResult;
import edu.fudan.poem_service.vo.ContestResultVo;

import java.util.List;

public interface ContestResultService {
    boolean saveContestResult(ContestResult contestResult);

    List<ContestResultVo> getUserContestResults(Long userId);

    List<Double> getUserContestWinRate(Long userId);

    Double getUserWinRateTotal(Long userId);

    List<Integer> getUserContestNum(Long userId);
}
