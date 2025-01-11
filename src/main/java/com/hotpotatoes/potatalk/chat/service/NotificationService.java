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

    public void saveNotification(String userId, String notificationMessage) {
        String notificationKey = NOTIFICATIONS_KEY_PREFIX + userId;
        redisTemplate.opsForList().leftPush(notificationKey, notificationMessage);
    }

    public NotificationSettings getNotificationSettings(String userId) {
        String notificationKey = NOTIFICATION_SETTINGS_KEY_PREFIX + userId;
        String notificationSetting = redisTemplate.opsForValue().get(notificationKey);

        if (notificationSetting == null) {
            return new NotificationSettings(true, true, true);
        }

        try {
            // JSON 문자열을 NotificationSettings 객체로 변환
            return objectMapper.readValue(notificationSetting, NotificationSettings.class);
        } catch (JsonProcessingException e) {
            return new NotificationSettings(true, true, true);  // 예외 발생 시 기본값
        }
    }

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


    public List<String> getAllNotifications(String userId) {
        String notificationKey = NOTIFICATIONS_KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(notificationKey, 0, -1);  // 리스트 전체 조회
    }
}
