package com.campus.campus_backend.controller;

import com.campus.campus_backend.dto.MatchRecordDTO;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.service.ChatRoomService;
import com.campus.campus_backend.service.MatchRequestService;
import com.campus.campus_backend.repository.MatchRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchRecordRepository matchRecordRepository;
    private final MatchRequestService matchRequestService;
    private final ChatRoomService chatRoomService;

    public MatchController(MatchRecordRepository matchRecordRepository,
                           MatchRequestService matchRequestService,
                           ChatRoomService chatRoomService) {
        this.matchRecordRepository = matchRecordRepository;
        this.matchRequestService = matchRequestService;
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/for-post/{postId}")
    public List<MatchRecordDTO> getMatchesForPost(
            @PathVariable Long postId,
            Principal principal
    ) {
        String userId = principal.getName();

        List<MatchRecord> matchList =
                matchRecordRepository.findByLostPost_IdOrFoundPost_Id(postId, postId);

        return matchList.stream().map(match -> {
            Long chatReqId = matchRequestService.getChatRequestIdForUser(match, userId);

            MatchRecordDTO dto = new MatchRecordDTO(match, chatReqId);

            // 🔥 Add UI fields
            dto.setDisplayStatus(matchRequestService.getStatusForUser(match, userId));

            // 🔥 Add Chat Room ID
            Long chatId = chatRoomService.getChatRoomIdForMatch(match.getId());
            dto.setChatId(chatId);

            return dto;
        }).toList();
    }
}
