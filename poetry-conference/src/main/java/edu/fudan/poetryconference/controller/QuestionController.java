package edu.fudan.poetryconference.controller;


import edu.fudan.poetryconference.model.Question;
import edu.fudan.poetryconference.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int id) {
        return questionService.getQuestionById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }
}
