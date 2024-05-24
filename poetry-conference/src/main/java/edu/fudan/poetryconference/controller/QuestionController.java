package edu.fudan.poetryconference.controller;


import edu.fudan.poetryconference.model.Questions;
import edu.fudan.poetryconference.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @CrossOrigin
    @GetMapping("/review/{userId}")
    public ResponseEntity<String> getReviewQuestion(@PathVariable("userId") Long userId) {
        String questionInfo = questionService.getReviewQuestion(userId);
        return ResponseEntity.ok(questionInfo);
    }
}
