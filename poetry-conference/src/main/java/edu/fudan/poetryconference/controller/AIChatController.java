package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.service.AIChatService;
import edu.fudan.poetryconference.service.GenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

@RestController
public class AIChatController {
    @Autowired
    private AIChatService aiChatService;

    @Autowired
    private GenerationService generationService;

    @CrossOrigin
    @GetMapping("/api/chatting")
    public Mono<String> chatWithAI(@RequestParam String message) {
        return aiChatService.getCompletion(message);
    }

    @CrossOrigin
    @GetMapping("/api/review/explain")
    public Mono<String> explainQuestion(@RequestParam String message) {
        return aiChatService.explainQuestion(message);
    }

    @CrossOrigin
    @GetMapping("/api/chat")
    public ResponseEntity<String> getMessage(@RequestParam String message) {
        try {
            String result = generationService.generateMessage(message);
            return ResponseEntity.ok(result);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // Log the exception, in a real scenario use a Logger
            return ResponseEntity.internalServerError().build();
        }
    }
}
