package edu.fudan.poem_service.service.impl;

import edu.fudan.poem_service.service.RoomService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoomServiceImpl implements RoomService {

    private static final String[] ROOM_IDS = {"room1", "room2", "room3", "room4"};

    // 使用二维数组来存储每个房间的角色状态
    private boolean[][] roomCounts = new boolean[ROOM_IDS.length][2];

    @PostConstruct
    public void init() {
        clearRoomCounts();
    }

    public void clearRoomCounts() {
        for (int i = 0; i < roomCounts.length; i++) {
            roomCounts[i][0] = false;
            roomCounts[i][1] = false;
        }
    }

    public void incrementRoomCount(String roomId, int role) {
        int index = getRoomIndex(roomId);
        if (index != -1) {
            roomCounts[index][role - 1] = true;
        }
    }

    public void decrementRoomCount(String roomId, int role) {
        int index = getRoomIndex(roomId);
        if (index != -1) {
            roomCounts[index][role - 1] = false;
        }
    }

    public int getRoomCount(String roomId) {
        int index = getRoomIndex(roomId);
        if (index != -1) {
            if (!roomCounts[index][0]) {
                return 0;
            }
            if (!roomCounts[index][1]) {
                return 1;
            }
            return 2;
        }
        return 0;
    }

    private int getRoomIndex(String roomId) {
        for (int i = 0; i < ROOM_IDS.length; i++) {
            if (ROOM_IDS[i].equals(roomId)) {
                return i;
            }
        }
        return -1; // 如果房间ID不存在，返回-1
    }
}
