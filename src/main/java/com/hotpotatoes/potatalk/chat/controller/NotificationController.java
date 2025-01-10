package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import com.hotpotatoes.potatalk.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // 사용자 알림 설정 조회
    @GetMapping("/settings/{userId}")
    public NotificationSettings getUserNotificationSettings(@PathVariable String userId) {
        return notificationService.getNotificationSettings(userId);
    }

    // 사용자 알림 설정 저장
    @PostMapping("/settings/{userId}")
    public String saveUserNotificationSettings(@PathVariable String userId, @RequestBody NotificationSettings settings) {
        notificationService.saveNotificationSettings(userId, settings);
        return "알림 설정이 저장되었습니다.";
    }

    // 사용자 전체 알림 조회
    @GetMapping("/{userId}")
    public List<String> getAllNotifications(@PathVariable String userId) {
        return notificationService.getAllNotifications(userId);
    }
}
