package edu.fudan.poem_service.service;

public interface RoomService {
    void incrementRoomCount(String roomId, int role);

    void decrementRoomCount(String roomId, int role);

    int getRoomCount(String roomId);
}
