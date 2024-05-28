package edu.fudan.poetryconference.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@ServerEndpoint(value = "/rtc")
@Component
public class WebRtcChannel implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebRtcChannel.applicationContext = applicationContext;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRtcChannel.class);

    private static final Map<Integer, Set<Session>> sessions = Collections.synchronizedMap(new HashMap<Integer, Set<Session>>() {{
        put(1, new CopyOnWriteArraySet<>());
        put(2, new CopyOnWriteArraySet<>());
        put(3, new CopyOnWriteArraySet<>());
        put(4, new CopyOnWriteArraySet<>());
    }});

    private Session session;
    private static final List<String> peers = Collections.synchronizedList(new ArrayList<>());

    private static Map<Integer, LocalDateTime> topicSent = Collections.synchronizedMap(new HashMap<Integer, LocalDateTime>() {{
        put(1, LocalDateTime.now());
        put(2, LocalDateTime.now());
        put(3, LocalDateTime.now());
        put(4, LocalDateTime.now());
    }});

    private static Map<Integer, Boolean> endSent = Collections.synchronizedMap(new HashMap<Integer, Boolean>() {{
        put(1, false);
        put(2, false);
        put(3, false);
        put(4, false);
    }});

    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> params = session.getRequestParameterMap();
        String roomStr = params.get("room").get(0);
        Integer room = Integer.parseInt(roomStr);
        this.session = session;
        sessions.get(room).add(session);
        LOGGER.info("[webrtc] New session opened: id={}", session.getId());

        peers.add(session.getId());
        // 广播新客户端加入
        sendToSession(session, createMessage("yourId", session.getId()));
        sendToSession(session, createMessage("introduction", peers));
        broadcastToRoomExceptSender(room, session, createMessage("peerConnection", session.getId()));
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String type = jsonNode.get("type").asText();
            String targetId = jsonNode.has("to") ? jsonNode.get("to").asText() : null;

            LOGGER.info("{} [webrtc] Received message type: {}", this.session.getId(), type);

            switch (type) {
                case "signal":
                    String fromId = this.session.getId();
                    String data = jsonNode.get("data").toString();
                    sendSignalMessage(targetId, fromId, data);
                    break;
                case "topic":
                    Integer room = getRoomId();
                    if (room != null) {
                        LocalDateTime lastSent = topicSent.get(room);
                        if (LocalDateTime.now().isAfter(lastSent.plusSeconds(10))) {
                            topicSent.put(room, LocalDateTime.now());
                            broadcastToRoom(room, message);
                        }
                    }
                    break;
                case "data":
                    // 处理数据消息并广播
                    broadcastToRoom(getRoomId(), message);
                    break;
                default:
                    LOGGER.warn("Unhandled message type: {}", type);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing message", e);
        }
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        Integer room = getRoomId();
        if (room != null) {
            sessions.get(room).remove(this.session);
            LOGGER.info("[webrtc] Session closed: id={}, reason={}", this.session.getId(), closeReason);
            // 在peers中删除离开的客户端
            peers.remove(this.session.getId());
            broadcastToRoom(room, createMessage("peerLeft", this.session.getId()));

        }
    }

    @OnError
    public void onError(Throwable throwable) {
        LOGGER.error("[webrtc] Error occurred: id={}, throwable={}", this.session.getId(), throwable.getMessage());
    }

    private Integer getRoomId() {
        for (Map.Entry<Integer, Set<Session>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(this.session)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void sendSignalMessage(String to, String from, String data) throws IOException {
        Integer room = getRoomId();
        if (room != null) {
            for (Session session : sessions.get(room)) {
                if (session.getId().equals(to)) {
                    session.getBasicRemote().sendText(createMessage("signal", from, data));
                    break;
                }
            }
        }
    }

    private String createMessage(String type, String from, String data) {
        return String.format("{\"type\": \"%s\", \"from\": \"%s\", \"data\": %s}", type, from, data);
    }

    private String createMessage(String type, List<String> peers) {
        return String.format("{\"type\": \"%s\", \"peers\": %s}", type, peers.toString());
    }

    private String createMessage(String type, String id) {
        return String.format("{\"type\": \"%s\", \"id\": \"%s\"}", type, id);
    }

    private void sendToSession(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.error("Error sending message to session {}", session.getId(), e);
        }
    }

    private void broadcastToRoom(Integer room, String message) {
        for (Session session : sessions.get(room)) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOGGER.error("Error broadcasting message to session {}", session.getId(), e);
            }
        }
    }

    private void broadcastToRoomExceptSender(Integer room, Session sender, String message) {
        for (Session session : sessions.get(room)) {
            if (!session.getId().equals(sender.getId())) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    LOGGER.error("Error broadcasting message to session {}", session.getId(), e);
                }
            }
        }
    }
}
