package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/{roomId}/increment")
    public ResponseEntity<String> incrementRoomCount(@PathVariable String roomId) {
        roomService.incrementRoomCount(roomId);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/{roomId}/decrement")
    public ResponseEntity<String> decrementRoomCount(@PathVariable String roomId) {
        roomService.decrementRoomCount(roomId);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/{roomId}/count")
    public ResponseEntity<Integer> getRoomCount(@PathVariable String roomId) {
        int count = roomService.getRoomCount(roomId);
        return ResponseEntity.ok(count);
    }
}
