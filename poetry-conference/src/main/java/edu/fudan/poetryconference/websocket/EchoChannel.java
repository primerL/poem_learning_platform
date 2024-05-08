package edu.fudan.poetryconference.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fudan.poetryconference.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/ws")
@Component
public class EchoChannel implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EchoChannel.applicationContext = applicationContext;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoChannel.class);

    // 使用 CopyOnWriteArraySet 保证线程安全
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    private Session session;

    private QuestionService service;

    private static boolean topicSent = false;

    @OnMessage
    public void onMessage(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
//            LOGGER.info("[websocket] Received JSON object: {}", jsonNode.toString());

            // 获取type字段的值
            JsonNode typeNode = jsonNode.get("type");
            if (typeNode != null) {
                String type = typeNode.asText();
                LOGGER.info("{} [websocket] Received message type: {}", this.session.getId(), type);

                if ("login".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "login");
                    data.put("socketId", this.session.getId());
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId());
                }
                else if ("position".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "position");
                    data.put("socketId", this.session.getId());
                    data.put("position", jsonNode.get("position").asText());
                    data.put("rotation", jsonNode.get("rotation").asText());
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId());
                }
                else if ("pre".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "pre");
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId());
                }
                else if ("depre".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "depre");
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId());
                }
                else if ("topic".equals(type)) {
                    if (topicSent) {
                        return;
                    }
                    topicSent = true;
                    this.service = EchoChannel.applicationContext.getBean(QuestionService.class);
                    broadcast(this.service.getQuestion(), "");
                    topicSent = false;
                }
                else if ("result".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "result");
                    data.put("name", jsonNode.get("name").asText());
                    data.put("result", jsonNode.get("result").asBoolean());
                    broadcast(objectMapper.writeValueAsString(data), "");
                }
                else if ("cont".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "cont");
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), "");
                }
            } else {
                LOGGER.warn("[websocket] Message does not contain 'type' field");
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("[websocket] Error parsing JSON: {}", e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        sessions.add(session);
        LOGGER.info("[websocket] New session opened: id={}", session.getId());
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "logout");
        data.put("socketId", this.session.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            broadcast(objectMapper.writeValueAsString(data), this.session.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sessions.remove(this.session);
        LOGGER.info("[websocket] Session closed: id={}, reason={}", this.session.getId(), closeReason);
    }

    @OnError
    public void onError(Throwable throwable) {
        LOGGER.error("[websocket] Error occurred: id={}, throwable={}", this.session.getId(), throwable.getMessage());
    }

    // 广播消息给所有连接的会话
    private static void broadcast(String message, String socketId) throws IOException {
        for (Session session : sessions) {
            if (session.getId().equals(socketId)) {
                continue;
            }
            session.getBasicRemote().sendText(message);
        }
    }
}
