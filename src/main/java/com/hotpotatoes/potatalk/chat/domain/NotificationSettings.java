package com.hotpotatoes.potatalk.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {

    private boolean matchNotificationEnabled; // 매칭 알림 설정
    private boolean messageNotificationEnabled; // 메시지 알림 설정
    private boolean waitingQueueNotificationEnabled; // 대기열 알림 설정
}
