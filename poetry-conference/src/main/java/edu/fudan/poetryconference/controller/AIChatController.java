package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AIChatController {
    @Autowired
    private AIChatService aiChatService;

    @CrossOrigin
    @GetMapping("/api/chat")
    public Mono<String> chatWithAI(@RequestParam String message) {
        return aiChatService.getCompletion(message);
    }

    @CrossOrigin
    @GetMapping("/api/review/explain")
    public Mono<String> explainQuestion(@RequestParam String message) {
        return aiChatService.explainQuestion(message);
    }
}
