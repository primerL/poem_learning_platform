package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.service.RoomService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ROOM_COUNT_PREFIX = "room_count:";

    // 房间ID列表
    private static final String[] ROOM_IDS = {"room1", "room2", "room3", "room4"};

    @PostConstruct
    public void init() {
        clearRoomCounts();
    }

    public void clearRoomCounts() {
        for (String roomId : ROOM_IDS) {
            redisTemplate.opsForValue().set(ROOM_COUNT_PREFIX + roomId, 0);
        }
    }

    public void incrementRoomCount(String roomId) {
        redisTemplate.opsForValue().increment(ROOM_COUNT_PREFIX + roomId);
    }

    public void decrementRoomCount(String roomId) {
        redisTemplate.opsForValue().decrement(ROOM_COUNT_PREFIX + roomId);
    }

    public int getRoomCount(String roomId) {
        Integer count = (Integer) redisTemplate.opsForValue().get(ROOM_COUNT_PREFIX + roomId);
        return count == null ? 0 : count;
    }
}
