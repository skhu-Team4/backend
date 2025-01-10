package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/match")
    public void handleMatchRequest(String userId) {
        String key = "default"; // 매칭 옵션에 따른 키 (예: "2:옵션A,옵션B")

        // 대기열에서 두 명의 랜덤 사용자 가져오기
        List<String> matchedUsers = matchingService.getRandomUsersFromWaitingList(key);

        if (matchedUsers != null && matchedUsers.size() == 2) {
            // 매칭 성공
            String user1 = matchedUsers.get(0);
            String user2 = matchedUsers.get(1);
            messagingTemplate.convertAndSend("/topic/match/" + user1, "매칭 성공: " + user2);
            messagingTemplate.convertAndSend("/topic/match/" + user2, "매칭 성공: " + user1);
        } else {
            // 대기열에 본인 등록
            matchingService.addUserToWaitingList(key, userId);
            messagingTemplate.convertAndSend("/topic/match/" + userId, "대기열에 등록되었습니다.");
        }
    }
}
