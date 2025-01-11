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
            NotificationSettings matchedUserSettings = notificationService.getNotificationSettings(matchedUser);

            messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭 성공: " + matchedUser);
            messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭 성공: " + userId);

            if (userSettings.isMatchNotificationEnabled()) {
                notificationService.saveNotification(userId, "매칭에 성공하였습니다.");
            }

            if (matchedUserSettings.isMatchNotificationEnabled()) {
                notificationService.saveNotification(userId, "매칭에 성공하였습니다.");
            }
        } else {
            matchingService.addUserToWaitingList(key, userId);

            messagingTemplate.convertAndSend("/topic/match/" + userId, "대기열에 등록되었습니다.");

            NotificationSettings userSettings = notificationService.getNotificationSettings(userId);
            if (userSettings.isWaitingQueueNotificationEnabled()) {
                notificationService.saveNotification(userId, "대기열에 등록되었습니다.");
            }
        }
    }

    @MessageMapping("/match/accept")
    public void acceptMatch(String userId, String matchedUser) {
        matchingService.acceptMatch(userId, matchedUser);

        messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭을 수락하였습니다.");
        messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭을 수락하였습니다.");

        NotificationSettings userSettings = notificationService.getNotificationSettings(userId);
        if (userSettings.isWaitingQueueNotificationEnabled()) {
            notificationService.saveNotification(userId, "매칭을 수락하였습니다.");
        }

        NotificationSettings matchedUserSettings = notificationService.getNotificationSettings(matchedUser);
        if (matchedUserSettings.isWaitingQueueNotificationEnabled()) {
            notificationService.saveNotification(matchedUser, "매칭을 수락하였습니다.");
        }
    }

    @MessageMapping("/match/reject")
    public void rejectMatch(String userId) {
        matchingService.rejectMatch(userId);

        messagingTemplate.convertAndSend("/topic/match/" + userId, "매칭을 거절하였습니다. 대기열에 다시 등록되었습니다.");

        NotificationSettings userSettings = notificationService.getNotificationSettings(userId);
        if (userSettings.isWaitingQueueNotificationEnabled()) {
            notificationService.saveNotification(userId, "매칭을 거절하였습니다. 대기열에 다시 등록되었습니다.");
        }
    }
}
