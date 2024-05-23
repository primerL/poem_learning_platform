package edu.fudan.poetryconference.service;

public interface RoomService {
    void incrementRoomCount(String roomId);

    void decrementRoomCount(String roomId);

    int getRoomCount(String roomId);
}
