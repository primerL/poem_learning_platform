package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.service.RoomService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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
        List<Boolean> initialState = Arrays.asList(false, false);
        for (String roomId : ROOM_IDS) {
            redisTemplate.opsForValue().set(ROOM_COUNT_PREFIX + roomId, initialState);
        }
    }

    public void incrementRoomCount(String roomId, int role) {
        String key = ROOM_COUNT_PREFIX + roomId;
        List<Boolean> roomCount = (List<Boolean>) redisTemplate.opsForValue().get(key);
        if (roomCount != null) {
            roomCount.set(role - 1, true);
            redisTemplate.opsForValue().set(key, roomCount);
        }
    }

    public void decrementRoomCount(String roomId, int role) {
        String key = ROOM_COUNT_PREFIX + roomId;
        List<Boolean> roomCount = (List<Boolean>) redisTemplate.opsForValue().get(key);
        if (roomCount != null) {
            roomCount.set(role - 1, false);
            redisTemplate.opsForValue().set(key, roomCount);
        }
    }

    public int getRoomCount(String roomId) {
        String key = ROOM_COUNT_PREFIX + roomId;
        List<Boolean> roomCount = (List<Boolean>) redisTemplate.opsForValue().get(key);
        if (roomCount != null) {
            if (!roomCount.get(0)){
                return 0;
            }
            if (!roomCount.get(1)){
                return 1;
            }
            return 2;
        }
        return 0;
    }
}
