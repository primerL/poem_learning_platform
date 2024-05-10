package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.model.WrongAnswer;
import edu.fudan.poetryconference.service.WrongAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wrong-answers")
public class WrongAnswerController {
    @Autowired
    private WrongAnswerService wrongAnswerService;

    @PostMapping
    public ResponseEntity<WrongAnswer> createWrongAnswer(@RequestBody WrongAnswer wrongAnswer) {
        WrongAnswer savedWrongAnswer = wrongAnswerService.saveWrongAnswer(wrongAnswer);
        return ResponseEntity.ok(savedWrongAnswer);
    }

    //获得某用户的所有错题
}
