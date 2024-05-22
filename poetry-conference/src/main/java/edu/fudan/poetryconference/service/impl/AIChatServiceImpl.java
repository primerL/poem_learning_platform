package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.APIResponse;
import edu.fudan.poetryconference.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AIChatServiceImpl implements AIChatService {
    @Autowired
    private WebClient webClient;

    public Mono<String> getCompletion(String userInput) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(buildRequestBody(userInput))
                .retrieve()
                .bodyToMono(APIResponse.class)  // 将响应转换为APIResponse类
                .map(response -> response.getChoices().get(0).getMessage().getContent()); // 从响应中提取content
    }

    @Override
    public Mono<String> explainQuestion(String userInput) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(buildExplainRequestBody(userInput))
                .retrieve()
                .bodyToMono(APIResponse.class)  // 将响应转换为APIResponse类
                .map(response -> response.getChoices().get(0).getMessage().getContent()); // 从响应中提取content
    }

    private String buildRequestBody(String userInput) {
        return "{"
                + "\"model\": \"moonshot-v1-8k\","
                + "\"messages\": ["
                + "{\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。\"},"
                + "{\"role\": \"user\", \"content\": \"" + userInput + "\"}"
                + "],"
                + "\"temperature\": 0.3"
                + "}";
    }

    private String buildExplainRequestBody(String userInput) {
        return "{"
                + "\"model\": \"moonshot-v1-8k\","
                + "\"messages\": ["
                + "{\"role\": \"system\", \"content\": \"你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。你擅长回答关于中国古诗词的问题。\"},"
                + "{\"role\": \"user\", \"content\": \"" + userInput + "\"}"
                + "],"
                + "\"temperature\": 0.3"
                + "}";
    }
}
