package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.security.UserDetailsImpl;
import com.campus.campus_backend.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService roomService;

    public ChatRoomController(ChatRoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatRoom>> getMyRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).build();
        }

        List<ChatRoom> rooms =
                roomService.getChatRoomsForUser(userDetails.getUser().getUserId());

        return ResponseEntity.ok(rooms);
    }
}
