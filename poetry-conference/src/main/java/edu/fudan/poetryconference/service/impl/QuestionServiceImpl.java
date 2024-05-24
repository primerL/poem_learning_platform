package edu.fudan.poetryconference.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fudan.poetryconference.model.Answers;
import edu.fudan.poetryconference.model.Options;
import edu.fudan.poetryconference.model.Questions;
import edu.fudan.poetryconference.repository.AnswersRepository;
import edu.fudan.poetryconference.repository.OptionRepository;
import edu.fudan.poetryconference.repository.QuestionRepository;
import edu.fudan.poetryconference.service.QuestionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswersRepository answerRepository;

    @Autowired
    private OptionRepository optionsRepository;
    public String getQuestion() {
        // 在225个题目中随机选取一个题目
        int questionId = (int) (Math.random() * 225) + 1;

        Questions questions = questionRepository.findById(questionId).orElse(null);
//        System.out.println(questions.getContent());
        List<Options> options = optionsRepository.findByQuestionId(questionId);
//        System.out.println(options.get(0).getContent());
        // 返回题目和选项，json格式
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap();
        map.put("type", "topic");
        map.put("question", questions.getContent());
        List<String> optionsList = options.stream().map(Options::getContent).toList();
        map.put("options", optionsList);
        map.put("questionId", questionId);
        map.put("answer", questions.getAnswer());

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public String getReviewQuestion(Long userId) {
        Answers answer = answerRepository.findLeastReviewedQuestionForUser(userId);
        if (answer == null) {
            return "{}"; // 或者一些错误处理
        }
        Long questionId = answer.getQuestionId();
        Long answerId = answer.getId();

        Map<String, Object> questionDetails = getQuestionAndOptions(questionId.intValue());
        questionDetails.put("type", "review");

        answerRepository.updateReviewTimesAndAnswerDateById(answerId);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(questionDetails);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getQuestionAndOptions(int questionId) {
        Questions questions = questionRepository.findById(questionId).orElse(null);
        List<Options> options = optionsRepository.findByQuestionId(questionId);

        List<String> optionsList = options.stream()
                .map(Options::getContent)
                .toList();

        Map<String, Object> questionDetails = new HashMap<>();
        questionDetails.put("question", questions.getContent());
        questionDetails.put("options", optionsList);
        questionDetails.put("answer", questions.getAnswer());
        questionDetails.put("questionId", questionId);

        return questionDetails;
    }

}
