package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.model.ContestResult;
import edu.fudan.poetryconference.service.ContestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contest-results")
public class ContestResultController {
    @Autowired
    private ContestResultService contestResultService;

    @PostMapping
    public ResponseEntity<ContestResult> createContestResult(@RequestBody ContestResult contestResult) {
        ContestResult savedResult = contestResultService.saveContestResult(contestResult);
        return ResponseEntity.ok(savedResult);
    }

    // Additional API endpoints can be implemented here
}