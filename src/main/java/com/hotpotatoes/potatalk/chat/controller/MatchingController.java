package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import com.hotpotatoes.potatalk.chat.service.MatchingService;
import com.hotpotatoes.potatalk.chat.service.NotificationService;
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
    private final NotificationService notificationService;

    @MessageMapping("/match")
    public void handleMatchRequest(String userId) {
        String key = "default"; // 매칭 옵션에 따른 키 (예: "2:옵션A,옵션B")

        // 대기열에서 두 명의 랜덤 사용자 가져오기
        List<String> matchedUsers = matchingService.getRandomUsersFromWaitingList(key);

        if (matchedUsers != null && matchedUsers.size() == 2) {
            // 매칭 성공
            String user1 = matchedUsers.get(0);
            String user2 = matchedUsers.get(1);

            // 매칭 성공 알림을 보내기 전에 사용자 알림 설정 확인
            NotificationSettings user1Settings = notificationService.getNotificationSettings(user1);
            NotificationSettings user2Settings = notificationService.getNotificationSettings(user2);

            if (user1Settings.isMatchNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + user1, "매칭 성공: " + user2);
            }

            if (user2Settings.isMatchNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + user2, "매칭 성공: " + user1);
            }

        } else {
            // 대기열에 본인 등록
            matchingService.addUserToWaitingList(key, userId);

            // 이미 대기열에 등록된 경우 알림 전송
            NotificationSettings userSettings = notificationService.getNotificationSettings(userId);

            if (userSettings.isWaitingQueueNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + userId, "대기열에 등록되었습니다.");
            }
        }
    }

    @MessageMapping("/match/accept")
    public void acceptMatch(String userId, String matchedUser) {
        // 매칭 수락 처리
        matchingService.acceptMatch(userId, matchedUser);

        // 수락된 사용자에게 알림 전송
        messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭을 수락하였습니다.");
        messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭을 수락하였습니다.");
    }

    @MessageMapping("/match/reject")
    public void rejectMatch(String userId) {
        // 매칭 거절 처리
        matchingService.rejectMatch(userId);

        // 거절된 사용자에게 알림 전송
        messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭을 거절하였습니다. 대기열에 다시 등록되었습니다.");
    }
}
