package edu.fudan.ai_chat_service.controller;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import edu.fudan.ai_chat_service.service.AIChatService;
import edu.fudan.ai_chat_service.service.GenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
public class AIChatController {
    @Autowired
    private AIChatService aiChatService;

    @Autowired
    private GenerationService generationService;

    @CrossOrigin
    @GetMapping("/chatting")
    public Mono<String> chatWithAI(@RequestParam String message) {
        return aiChatService.getCompletion(message);
    }

    @CrossOrigin
    @GetMapping("/review/explain")
    public Mono<String> explainQuestion(@RequestParam String message) {
        return aiChatService.explainQuestion(message);
    }

    @CrossOrigin
    @GetMapping("/for/help")
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