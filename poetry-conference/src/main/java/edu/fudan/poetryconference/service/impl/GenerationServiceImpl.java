package edu.fudan.poetryconference.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import edu.fudan.poetryconference.service.GenerationService;
import org.springframework.stereotype.Service;
import com.alibaba.dashscope.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;

@Service
public class GenerationServiceImpl implements GenerationService {
    public String generateMessage(String question_content) throws ApiException, NoApiKeyException, InputRequiredException {
        Constants.apiKey="sk-fb566bdc42984fb78b95cc00e037975a";
        Generation gen = new Generation();

        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个擅长中国诗歌的智慧助手，你的名字叫螺丝咕姆.")
                .build();

        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(question_content)
                .build();

        GenerationParam param = GenerationParam.builder()
                .model("qwen-turbo")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .build();

        GenerationResult result = gen.call(param);

        //输出result
        System.out.println(result.toString());
        GenerationOutput output = result.getOutput();
        GenerationOutput.Choice choice = output.getChoices().get(0);
        return choice.getMessage().getContent();
    }

    public static String extractContent(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);

            // Navigate through the structure as per the JSON you provided
            JsonNode choicesNode = rootNode.path("output").path("choices");
            if (choicesNode.isArray()) {
                // Assuming you want the first choice's content
                JsonNode contentNode = choicesNode.get(0).path("message").path("content");
                return contentNode.asText();  // Convert the content node to a String
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // In case of an error, return null or handle it as needed
        }
        return null;
    }

}
