package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import com.hotpotatoes.potatalk.chat.dto.chat.req.MatchAcceptRequestDto;
import com.hotpotatoes.potatalk.chat.dto.match.req.MatchRequestDto;
import com.hotpotatoes.potatalk.chat.service.MatchingService;
import com.hotpotatoes.potatalk.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @MessageMapping("/match")
    public void handleMatchRequest(MatchRequestDto RequestDto) {
        String key = "default"; // 매칭 옵션에 따른 키

        String matchedUser = matchingService.getRandomUserFromWaitingList(key);

        if (matchedUser != null) {
            NotificationSettings userSettings = notificationService.getNotificationSettings(RequestDto.getUserId());
            NotificationSettings matchedUserSettings = notificationService.getNotificationSettings(matchedUser);

            messagingTemplate.convertAndSend("/topic/match/" + RequestDto.getUserId(), "매칭 성공: " + matchedUser);
            messagingTemplate.convertAndSend("/topic/match/" + matchedUser, "매칭 성공: " + RequestDto.getUserId());

            if (userSettings.isMatchNotificationEnabled()) {
                notificationService.saveNotification(RequestDto.getUserId(), "매칭에 성공하였습니다.");
            }

            if (matchedUserSettings.isMatchNotificationEnabled()) {
                notificationService.saveNotification(matchedUser, "매칭에 성공하였습니다.");
            }
        } else {
            matchingService.addUserToWaitingList(key, RequestDto.getUserId());

            messagingTemplate.convertAndSend("/topic/match/" + RequestDto.getUserId(), "대기열에 등록되었습니다.");

            NotificationSettings userSettings = notificationService.getNotificationSettings(RequestDto.getUserId());
            if (userSettings.isWaitingQueueNotificationEnabled()) {
                notificationService.saveNotification(RequestDto.getUserId(), "대기열에 등록되었습니다.");
            }
        }
    }

    @MessageMapping("/match/accept")
    public void acceptMatch(MatchAcceptRequestDto requestDto) {
        matchingService.acceptMatch(requestDto.getUserId(), requestDto.getMatchedUser());

        messagingTemplate.convertAndSend("/topic/match/" + requestDto.getUserId(), "매칭을 수락하였습니다.");
        messagingTemplate.convertAndSend("/topic/match/" + requestDto.getMatchedUser(), "매칭을 수락하였습니다.");

        NotificationSettings userSettings = notificationService.getNotificationSettings(requestDto.getUserId());
        if (userSettings.isWaitingQueueNotificationEnabled()) {
            notificationService.saveNotification(requestDto.getUserId(), "매칭을 수락하였습니다.");
        }

        NotificationSettings matchedUserSettings = notificationService.getNotificationSettings(requestDto.getMatchedUser());
        if (matchedUserSettings.isWaitingQueueNotificationEnabled()) {
            notificationService.saveNotification(requestDto.getMatchedUser(), "매칭을 수락하였습니다.");
        }
    }

    @MessageMapping("/match/reject")
    public void rejectMatch(MatchRequestDto matchRequestDto) {
        matchingService.rejectMatch(matchRequestDto.getUserId());

        messagingTemplate.convertAndSend("/topic/match/" + matchRequestDto.getUserId(), "매칭을 거절하였습니다. 대기열에 다시 등록되었습니다.");

        NotificationSettings userSettings = notificationService.getNotificationSettings(matchRequestDto.getUserId());
        if (userSettings.isWaitingQueueNotificationEnabled()) {
            notificationService.saveNotification(matchRequestDto.getUserId(), "매칭을 거절하였습니다. 대기열에 다시 등록되었습니다.");
        }
    }
}
