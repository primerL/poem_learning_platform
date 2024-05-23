package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.dto.UserContestResultDTO;
import edu.fudan.poetryconference.model.ContestResult;
import edu.fudan.poetryconference.repository.ContestResultRepository;
import edu.fudan.poetryconference.service.ContestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ContestResultServiceImpl implements ContestResultService {
    @Autowired
    private ContestResultRepository contestResultRepository;

    @Override
    public boolean saveContestResult(ContestResult contestResult) {
        contestResultRepository.save(contestResult);
        return true;
    }

    @Override
    public List<UserContestResultDTO> getUserContestResults(Long userId) {
        List<ContestResult> contestResults = contestResultRepository.findByUser1Id(userId);
        assert(contestResults.size() > 0);
        System.out.println(contestResults.size());
        return contestResults.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Double getUserWinRateTotal(Long userId) {
        List<ContestResult> contestResults = contestResultRepository.findByUser1Id(userId);
        assert(contestResults.size() > 0);
        long countTotal = contestResults.size();  // 总比赛数
        long countScore1Greater = contestResults.stream()
                .filter(result -> result.getScore1() > result.getScore2())
                .count();  // score1 > score2 的比赛数
        System.out.println("count size: "+countTotal);
        if (countTotal > 0) {
            double ratio = (double) countScore1Greater / countTotal;  // 计算比例
            return ratio;
        } else {
            return 0.0;
        }
    }

    @Override
    public List<Integer> getUserContestNum(Long userId) {
        LocalDateTime now = LocalDateTime.now();  // 获取当前日期
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime lastMonday = midnight.with(ChronoField.DAY_OF_WEEK, 1).minusWeeks(1);
        System.out.println("lastMondayDateTime: "+lastMonday);

        List<ContestResult> contestResults = contestResultRepository.findRecentContestsByUserId(userId,
                lastMonday);
        System.out.println("contestResults size: "+contestResults.size());
        List<Integer> numList = convertToNum(contestResults);
        return numList;
    }

    @Override
    public List<Double> getUserContestWinRate(Long userId) {
        LocalDateTime now = LocalDateTime.now();  // 获取当前日期
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime lastMonday = midnight.with(ChronoField.DAY_OF_WEEK, 1).minusWeeks(1);

        List<ContestResult> contestResults = contestResultRepository.findRecentContestsByUserId(userId,
                lastMonday);
        List<Double> winRateList = convertToWinRate(contestResults);
        return winRateList;
    }

    private UserContestResultDTO convertToDTO(ContestResult contestResult) {
        UserContestResultDTO dto = new UserContestResultDTO();
        dto.setUserId(contestResult.getUser1Id());
        dto.setScore(contestResult.getScore1());
        dto.setTime(contestResult.getContestDate());
        return dto;
    }

    private List<Integer> convertToNum(List<ContestResult> contestResults) {
        List<Integer> weeklyContestCounts = new ArrayList<>(7);
        // 初始化每天的比赛次数为0
        for (int i = 0; i < 7; i++) {
            weeklyContestCounts.add(0);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime lastMonday = midnight.with(ChronoField.DAY_OF_WEEK, 1).minusWeeks(1); // 上周一
        LocalDateTime nextMonday = lastMonday.plusWeeks(1);
        System.out.println("lastMonday: "+lastMonday);
        System.out.println("nextMonday: "+nextMonday);

        for (ContestResult contest : contestResults) {
            if (contest.getContestDate().isAfter(lastMonday) && contest.getContestDate().isBefore(nextMonday)) {
                int dayIndex = contest.getContestDate().getDayOfWeek().getValue() - 1; // DayOfWeek 返回的是1到7
                weeklyContestCounts.set(dayIndex, weeklyContestCounts.get(dayIndex) + 1); // 增加对应天的计数
            }
        }

        return weeklyContestCounts;
    }

    private List<Double> convertToWinRate(List<ContestResult> contestResults) {
        List<Double> weeklyWinRates = new ArrayList<>(7);
        // 初始化每天的胜率为0
        for (int i = 0; i < 7; i++) {
            weeklyWinRates.add(0.0);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime lastMonday = midnight.with(ChronoField.DAY_OF_WEEK, 1).minusWeeks(1); // 上周一
        LocalDateTime nextMonday = lastMonday.plusWeeks(1);

        for (ContestResult contest : contestResults) {
            if (contest.getContestDate().isAfter(lastMonday) && contest.getContestDate().isBefore(nextMonday)) {
                int dayIndex = contest.getContestDate().getDayOfWeek().getValue() - 1; // DayOfWeek 返回的是1到7
                Long score1 = contest.getScore1();
                Long score2 = contest.getScore2();
                if (score1 > score2) {
                    weeklyWinRates.set(dayIndex, weeklyWinRates.get(dayIndex) + 1.0); // 增加对应天的胜率
                }
            }
        }

        List<Integer> weeklyContestCounts = convertToNum(contestResults);

        for (int i = 0; i < weeklyWinRates.size(); i++) {
            if (weeklyContestCounts.get(i) > 0) {
                weeklyWinRates.set(i, weeklyWinRates.get(i) / weeklyContestCounts.get(i)); // 计算每天的胜率
            }
        }
        return weeklyWinRates;
    }


}
