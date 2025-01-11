package com.hotpotatoes.potatalk.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MatchingService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String WAITING_LIST_PREFIX = "waiting_list:";
    private static final String MATCHED_LIST_PREFIX = "matched_list:"; // 매칭된 사용자 목록

    // 대기열에 사용자 추가
    public void addUserToWaitingList(String key, String userId) {
        double randomScore = ThreadLocalRandom.current().nextDouble(0, 1); // 랜덤 점수 생성
        redisTemplate.opsForZSet().add(WAITING_LIST_PREFIX + key, userId, randomScore);
    }

    // 대기열에서 한 명의 사용자 가져오기
    public String getRandomUserFromWaitingList(String key) {
        String redisKey = WAITING_LIST_PREFIX + key;

        // 대기열에서 1명을 가져옴 (대기열에 사용자가 없으면 null 반환)
        Set<String> matchedUsers = redisTemplate.opsForZSet().range(redisKey, 0, 0);  // 한 명만 가져옴

        if (matchedUsers == null || matchedUsers.isEmpty()) {
            return null;  // 대기열에 사용자가 없으면 null 반환
        }

        // 첫 번째 사용자 가져오기
        String selectedUser = matchedUsers.iterator().next(); // Set에서 첫 번째 요소 가져오기

        // 매칭 성공 시 Redis 대기열에서 제거
        redisTemplate.opsForZSet().remove(redisKey, selectedUser);

        return selectedUser;
    }

    // 매칭 수락
    public void acceptMatch(String userId, String matchedUser) {
        // 매칭된 사용자 처리 로직 (예: 두 사람을 매칭된 상태로 저장)
        String matchKey = "matched:" + userId + ":" + matchedUser;
        redisTemplate.opsForValue().set(matchKey, "MATCHED");
    }

    // 매칭 거절
    public void rejectMatch(String userId) {
        // 대기열에 다시 사용자 추가
        addUserToWaitingList("default", userId); // "default"는 대기열의 키
    }

    // 매칭 완료 후 사용자 처리
    public void completeMatch(String user1, String user2) {
        // 대기열에서 사용자 제거
        redisTemplate.opsForZSet().remove(WAITING_LIST_PREFIX + "default", user1, user2);

        // 매칭 완료 목록에 추가 (매칭된 사용자)
        redisTemplate.opsForSet().add(MATCHED_LIST_PREFIX + "default", user1, user2);
    }

    // 매칭 완료 후 사용자에게 알림 전송
    public void notifyMatchCompletion(String userId, String matchedUser) {
        // 매칭 성공 알림
        redisTemplate.convertAndSend("/topic/match/" + userId, "매칭 성공: " + matchedUser);
    }

    // 매칭 거절 후 사용자에게 알림 전송
    public void notifyMatchRejection(String userId) {
        // 매칭 거절 알림
        redisTemplate.convertAndSend("/topic/match/" + userId, "매칭 거절: 대기열에 다시 등록되었습니다.");
    }
}
