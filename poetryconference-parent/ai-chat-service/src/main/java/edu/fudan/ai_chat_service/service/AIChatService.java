package edu.fudan.ai_chat_service.service;

import reactor.core.publisher.Mono;

public interface AIChatService {
    Mono<String> getCompletion(String userInput);

    Mono<String> explainQuestion(String userInput);
}