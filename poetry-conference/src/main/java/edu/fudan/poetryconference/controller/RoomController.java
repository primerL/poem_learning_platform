package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @CrossOrigin
    @GetMapping("/{roomId}/count")
    public ResponseEntity<Integer> getRoomCount(@PathVariable String roomId) {
        int count = roomService.getRoomCount(roomId);
        return ResponseEntity.ok(count);
    }
}
