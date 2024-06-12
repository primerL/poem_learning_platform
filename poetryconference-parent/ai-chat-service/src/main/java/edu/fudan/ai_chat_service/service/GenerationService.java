package edu.fudan.ai_chat_service.service;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

public interface GenerationService {
    String generateMessage(String question_content) throws ApiException, NoApiKeyException, InputRequiredException;
}