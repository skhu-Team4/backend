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
        String key = "default"; // 매칭 옵션에 따른 키

        String matchedUser = matchingService.getRandomUserFromWaitingList(key);

        if (matchedUser != null) {
            NotificationSettings userSettings = notificationService.getNotificationSettings(userId);
            if (userSettings.isMatchNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭 성공: " + matchedUser);
            }

            NotificationSettings matchedUserSettings = notificationService.getNotificationSettings(matchedUser);
            if (matchedUserSettings.isMatchNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭 성공: " + userId);
            }
        } else {
            matchingService.addUserToWaitingList(key, userId);

            NotificationSettings userSettings = notificationService.getNotificationSettings(userId);

            if (userSettings.isWaitingQueueNotificationEnabled()) {
                messagingTemplate.convertAndSend("/topic/match/" + userId, "대기열에 등록되었습니다.");
            }
        }
    }

    @MessageMapping("/match/accept")
    public void acceptMatch(String userId, String matchedUser) {
        matchingService.acceptMatch(userId, matchedUser);

        messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭을 수락하였습니다.");
        messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭을 수락하였습니다.");
    }

    @MessageMapping("/match/reject")
    public void rejectMatch(String userId) {
        matchingService.rejectMatch(userId);

        messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭을 거절하였습니다. 대기열에 다시 등록되었습니다.");
    }
}
