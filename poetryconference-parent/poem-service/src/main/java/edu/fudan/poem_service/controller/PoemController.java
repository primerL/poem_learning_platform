package edu.fudan.poem_service.controller;

import edu.fudan.poem_service.service.ContestResultService;
import edu.fudan.poem_service.service.QuestionService;
import edu.fudan.poem_service.service.RoomService;
import edu.fudan.poem_service.vo.ContestResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/poem")
public class PoemController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private ContestResultService contestResultService;

    @Autowired
    private RoomService roomService;

    @CrossOrigin
    @GetMapping("/review/{userId}")
    public ResponseEntity<String> getReviewQuestion(@PathVariable("userId") Long userId) {
        String questionInfo = questionService.getReviewQuestion(userId);
        return ResponseEntity.ok(questionInfo);
    }

    @CrossOrigin
    @GetMapping("/personal/contest/results/{userId}")
    public ResponseEntity<List<ContestResultVo>> getUserContestResults(@PathVariable Long userId){
        List<ContestResultVo> results = contestResultService.getUserContestResults(userId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/win/rate/total/{userId}")
    public Double getUserWinRateTotal(@PathVariable Long userId) {
        System.out.println("controller: "+userId);
        return contestResultService.getUserWinRateTotal(userId);
    }

    @CrossOrigin
    @GetMapping("/contest/num/{userId}")
    public ResponseEntity<List<Integer>> getUserContestNum(@PathVariable Long userId) {
        List<Integer> userContestNum = contestResultService.getUserContestNum(userId);
        return new ResponseEntity<>(userContestNum, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/contest/win/rate/{userId}")
    public ResponseEntity<List<Double>> getUserContestWinRate(@PathVariable Long userId){
        List<Double> userContestWinRate = contestResultService.getUserContestWinRate(userId);
        return new ResponseEntity<>(userContestWinRate, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{roomId}/count")
    public ResponseEntity<Integer> getRoomCount(@PathVariable String roomId) {
        int count = roomService.getRoomCount(roomId);
        return ResponseEntity.ok(count);
    }
}
