package edu.fudan.poem_service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fudan.poem_service.entity.Answers;
import edu.fudan.poem_service.entity.ContestResult;
import edu.fudan.poem_service.service.AnswersService;
import edu.fudan.poem_service.service.ContestResultService;
import edu.fudan.poem_service.service.QuestionService;
import edu.fudan.poem_service.service.RoomService;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws")
@Component
public class EchoChannel implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EchoChannel.applicationContext = applicationContext;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoChannel.class);

    // 使用 ConcurrentHashMap 和 CopyOnWriteArraySet 保证线程安全
    private static final Map<Integer, Set<Session>> sessions = Collections.synchronizedMap(new HashMap<Integer, Set<Session>>() {{
        put(1, new CopyOnWriteArraySet<>());
        put(2, new CopyOnWriteArraySet<>());
        put(3, new CopyOnWriteArraySet<>());
        put(4, new CopyOnWriteArraySet<>());
    }});

    private Session session;

    private static final Map<Integer, LocalDateTime> topicSent = Collections.synchronizedMap(new HashMap<Integer, LocalDateTime>() {{
        put(1, LocalDateTime.now());
        put(2, LocalDateTime.now());
        put(3, LocalDateTime.now());
        put(4, LocalDateTime.now());
    }});

    private static final Map<Integer, Boolean> endSent = Collections.synchronizedMap(new HashMap<Integer, Boolean>() {{
        put(1, false);
        put(2, false);
        put(3, false);
        put(4, false);
    }});

    private static Map<String, Integer> socketIdToRole = Collections.synchronizedMap(new HashMap<String, Integer>());

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
                if (!Objects.equals(type, "position")) {
                    LOGGER.info("{} [websocket] Received message type: {}", this.session.getId(), type);
                }

                if ("login".equals(type)) {
                    Integer role = jsonNode.get("role").asInt();
                    String socketId = this.session.getId();
                    if (role != 0) {
                        socketIdToRole.put(socketId, role);
                        //RoomService roomService = EchoChannel.applicationContext.getBean(RoomService.class);
                        //roomService.incrementRoomCount("room" + jsonNode.get("room").asInt(), role);
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "login");
                    data.put("socketId", socketId);
                    data.put("role", role);
                    data.put("name", jsonNode.get("name").asText());
                    data.put("userId", jsonNode.get("userId").asLong());
                    data.put("modelId", jsonNode.get("modelId").asLong());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), jsonNode.get("room").asInt());
                    // 单独传一个socketId给新登录的用户
                    Map<String, Object> data2 = new HashMap<>();
                    data2.put("type", "socketId");
                    data2.put("socketId", socketId);
                    this.session.getBasicRemote().sendText(objectMapper.writeValueAsString(data2));
                }
                else if ("position".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "position");
                    data.put("socketId", this.session.getId());
                    data.put("position", jsonNode.get("position").asText());
                    data.put("rotation", jsonNode.get("rotation").asText());
                    data.put("role", jsonNode.get("role").asLong());
                    data.put("name", jsonNode.get("name").asText());
                    data.put("userId", jsonNode.get("userId").asLong());
                    data.put("modelId", jsonNode.get("modelId").asLong());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), jsonNode.get("room").asInt());
                }
                else if ("still".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "still");
                    data.put("socketId", this.session.getId());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), jsonNode.get("room").asInt());
                }
                else if ("pre".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "pre");
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), jsonNode.get("room").asInt());
                }
                else if ("depre".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "depre");
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), jsonNode.get("room").asInt());
                }
                else if ("topic".equals(type)) {
                    int roomId = jsonNode.get("room").asInt();
                    // 两次发送题目间隔至少 5 秒
                    synchronized (topicSent) {
                        LocalDateTime lastSent = topicSent.get(roomId);

                        if (lastSent != null && LocalDateTime.now().isBefore(lastSent.plusSeconds(5))) {
                            return;
                        }

                        topicSent.put(roomId, LocalDateTime.now());
                    }
                    topicSent.put(roomId, LocalDateTime.now());
                    QuestionService questionService = EchoChannel.applicationContext.getBean(QuestionService.class);
                    broadcast(questionService.getQuestion(), "", jsonNode.get("room").asInt());
                }
                else if ("result".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    Answers answers = new Answers(jsonNode.get("userId").asLong(), jsonNode.get("questionId").asLong(), jsonNode.get("result").asBoolean());
                    AnswersService answersService = EchoChannel.applicationContext.getBean(AnswersService.class);
                    answersService.saveAnswers(answers);

                    data.put("type", "result");
                    data.put("name", jsonNode.get("name").asText());
                    data.put("result", jsonNode.get("result").asBoolean());
                    broadcast(objectMapper.writeValueAsString(data), "", jsonNode.get("room").asInt());
                }
                else if ("cont".equals(type)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "cont");
                    data.put("role", jsonNode.get("role").asText());
                    data.put("name", jsonNode.get("name").asText());
                    broadcast(objectMapper.writeValueAsString(data), "", jsonNode.get("room").asInt());
                }
                else if ("end".equals(type)) {
                    ContestResult contestResult = new ContestResult(jsonNode.get("user1Id").asLong(),
                            jsonNode.get("user2Id").asLong(), jsonNode.get("winNum").asLong(), jsonNode.get("loseNum").asLong());
                    ContestResultService contestResultService = EchoChannel.applicationContext.getBean(ContestResultService.class);
                    contestResultService.saveContestResult(contestResult);
                    if (endSent.get(jsonNode.get("room").asInt())) {
                        return;
                    }
                    endSent.put(jsonNode.get("room").asInt(), true);
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "end");
                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), jsonNode.get("room").asInt());
                    endSent.put(jsonNode.get("room").asInt(), false);
                }
//                else if ("logout".equals(type)) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("type", "logout");
//                    data.put("socketId", this.session.getId());
//                    Integer room = jsonNode.get("room").asInt();
//                    RoomService roomService = EchoChannel.applicationContext.getBean(RoomService.class);
//                    roomService.decrementRoomCount("room" + jsonNode.get("room").asInt());
//                    broadcast(objectMapper.writeValueAsString(data), this.session.getId(), room);
//                    sessions.get(room).remove(this.session);
//                }
                else if("flower".equals(type)) {
                    //
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "flower");
                    data.put("flower", jsonNode.get("flower").asText());
                    broadcast(objectMapper.writeValueAsString(data), "", jsonNode.get("room").asInt());
                }
                else if("chat".equals(type)) {
                    //
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "chat");
                    data.put("socketId", this.session.getId());
                    data.put("content", jsonNode.get("content").asText());
//                    data.put("from", jsonNode.get("from").asText());
                    data.put("to", jsonNode.get("to").asText());
                    broadcast(objectMapper.writeValueAsString(data), "", jsonNode.get("room").asInt());
                }
                else {
                    LOGGER.warn("[websocket] Unknown message type: {}", type);
                }
            } else {
                LOGGER.warn("[websocket] Message does not contain 'type' field");
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("[websocket] Error parsing JSON: {}", e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> params = session.getRequestParameterMap();
        String roomStr = params.get("room").get(0);
        Integer room = Integer.parseInt(roomStr);
        this.session = session;
        sessions.get(room).add(session);
        LOGGER.info("[websocket] New session opened: id={}", session.getId());
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        String socketId = this.session.getId();
        Map<String, Object> data = new HashMap<>();
        data.put("type", "logout");
        data.put("socketId", socketId);
        ObjectMapper objectMapper = new ObjectMapper();
        Integer room = 0;
        // 遍历所有房间，找到并删除当前会话
        for (Map.Entry<Integer, Set<Session>> entry : sessions.entrySet()) {
            Set<Session> sessionSet = entry.getValue();
            if (sessionSet.contains(this.session)) {
                room = entry.getKey();
                break;
            }
        }

        RoomService roomService = EchoChannel.applicationContext.getBean(RoomService.class);
        roomService.decrementRoomCount("room" + room, socketIdToRole.get(socketId));
        socketIdToRole.remove(this.session.getId());
        try {
            broadcast(objectMapper.writeValueAsString(data), socketId, room);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sessions.get(room).remove(this.session);
        LOGGER.info("[websocket] Session closed: id={}, reason={}", socketId, closeReason);
    }

    @OnError
    public void onError(Throwable throwable) {
        LOGGER.error("[websocket] Error occurred: id={}, throwable={}", this.session.getId(), throwable.getMessage());
    }

    // 广播消息给所有连接的会话
    private static void broadcast(String message, String socketId, Integer room) throws IOException {
        Set<Session> roomSessions = sessions.get(room);
        if (roomSessions == null) {
            return; // 如果房间不存在或者没有用户，则直接返回
        }

        for (Session session : roomSessions) {
            if (session.getId().equals(socketId)) {
                continue;
            }
            session.getBasicRemote().sendText(message);
        }
    }
}