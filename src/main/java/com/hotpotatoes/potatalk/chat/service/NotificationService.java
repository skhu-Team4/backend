package com.hotpotatoes.potatalk.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String NOTIFICATION_SETTINGS_KEY_PREFIX = "notification_settings:";
    private static final String NOTIFICATIONS_KEY_PREFIX = "notifications:";

    // Jackson ObjectMapper를 사용해 NotificationSettings 객체를 JSON 문자열로 변환
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 알림 저장 메서드
    public void saveNotification(String userId, String notificationMessage) {
        String notificationKey = NOTIFICATIONS_KEY_PREFIX + userId;
        redisTemplate.opsForList().leftPush(notificationKey, notificationMessage);
    }

    // 알림 설정 조회 메서드 (JSON 문자열을 객체로 변환)
    public NotificationSettings getNotificationSettings(String userId) {
        String notificationKey = NOTIFICATION_SETTINGS_KEY_PREFIX + userId;
        String notificationSetting = redisTemplate.opsForValue().get(notificationKey);

        if (notificationSetting == null) {
            // 기본 알림 설정 반환 (없으면 기본값 사용)
            return new NotificationSettings(true, true, true);
        }

        try {
            // JSON 문자열을 NotificationSettings 객체로 변환
            return objectMapper.readValue(notificationSetting, NotificationSettings.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new NotificationSettings(true, true, true);  // 예외 발생 시 기본값
        }
    }

    // 알림 설정 저장 메서드 (NotificationSettings 객체를 JSON 형식으로 저장)
    public void saveNotificationSettings(String userId, NotificationSettings settings) {
        String notificationKey = NOTIFICATION_SETTINGS_KEY_PREFIX + userId;

        try {
            // NotificationSettings 객체를 JSON 문자열로 변환하여 Redis에 저장
            String notificationSettingJson = objectMapper.writeValueAsString(settings);
            redisTemplate.opsForValue().set(notificationKey, notificationSettingJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // 알림 삭제 메서드
    public void deleteNotification(String userId) {
        String notificationKey = NOTIFICATIONS_KEY_PREFIX + userId;
        redisTemplate.delete(notificationKey);
    }

    // 전체 알림 조회 메서드
    public List<String> getAllNotifications(String userId) {
        String notificationKey = NOTIFICATIONS_KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(notificationKey, 0, -1);  // 리스트 전체 조회
    }
}
