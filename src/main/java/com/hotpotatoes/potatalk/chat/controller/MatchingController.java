package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import com.hotpotatoes.potatalk.chat.service.MatchingService;
import com.hotpotatoes.potatalk.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @MessageMapping("/match")
    public void handleMatchRequest(String userId) {
        String key = "default"; // 매칭 옵션에 따른 키 (예: "2:옵션A,옵션B")

        // 대기열에서 1명 이상의 랜덤 사용자 가져오기 (자기 자신을 제외한 대기열에서 한 명을 가져옵니다)
        String matchedUser = matchingService.getRandomUserFromWaitingList(key);

        if (matchedUser != null) {
            // 매칭 성공
            NotificationSettings userSettings = notificationService.getNotificationSettings(userId);
            if (userSettings.isMatchNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭 성공: " + matchedUser);
            }

            NotificationSettings matchedUserSettings = notificationService.getNotificationSettings(matchedUser);
            if (matchedUserSettings.isMatchNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭 성공: " + userId);
            }
        } else {
            // 대기열에 본인 등록
            matchingService.addUserToWaitingList(key, userId);

            // 대기열에 등록된 사용자에게 알림 전송
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
