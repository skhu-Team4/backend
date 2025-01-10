package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/match")
    public void handleMatchRequest(String userId) {
        String key = "default"; // 매칭 옵션에 따른 키 (예: "2:옵션A,옵션B")

        // 대기열에서 랜덤 사용자 가져오기
        String matchedUser = matchingService.getRandomUserFromWaitingList(key);

        if (matchedUser != null) {
            // 매칭 성공
            messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭 성공: " + userId);
            messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭 성공: " + matchedUser);
        } else {
            // 대기열에 본인 등록
            matchingService.addUserToWaitingList(key, userId);
            messagingTemplate.convertAndSend("/topic/match/" + userId, "대기열에 등록되었습니다.");
        }
    }
}
