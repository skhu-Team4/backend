package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import com.hotpotatoes.potatalk.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/settings/{userId}")
    public NotificationSettings getUserNotificationSettings(@PathVariable String userId) {
        return notificationService.getNotificationSettings(userId);
    }

    @PostMapping("/settings/{userId}")
    public String saveUserNotificationSettings(@PathVariable String userId, @RequestBody NotificationSettings settings) {
        notificationService.saveNotificationSettings(userId, settings);
        return "알림 설정이 저장되었습니다.";
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getAllNotificationsByUserId(@PathVariable("userId") String userId) {
        List<String> notifications = notificationService.getAllNotifications(userId);
        if (notifications.isEmpty()) {
            return ResponseEntity.ok(notifications);
        }
        return ResponseEntity.ok(notifications);
    }
}
