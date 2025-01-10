package com.hotpotatoes.potatalk.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // 랜덤으로 사용자 가져오기 (매칭 시)
    public List<String> getRandomUsersFromWaitingList(String key) {
        String redisKey = WAITING_LIST_PREFIX + key;

        // ZRANDMEMBER 호출 (결과를 List로 받음)
        List<String> randomUsers = redisTemplate.opsForZSet().randomMembers(redisKey, 2);

        if (randomUsers == null || randomUsers.size() < 2) {
            return null; // 대기열에 2명 미만이 있을 경우 매칭 불가
        }

        // 매칭 성공 시 Redis 대기열에서 제거
        redisTemplate.opsForZSet().remove(redisKey, randomUsers.get(0), randomUsers.get(1));

        // 매칭된 사용자 목록에 추가
        redisTemplate.opsForSet().add(MATCHED_LIST_PREFIX + key, randomUsers.get(0), randomUsers.get(1));

        return randomUsers;
    }

    // 매칭 수락
    public void acceptMatch(String user1, String user2) {
        // 매칭 완료 처리
        completeMatch(user1, user2);

        // 매칭 수락 후 알림
        notifyMatchCompletion(user1, user2);
        notifyMatchCompletion(user2, user1);
    }

    // 매칭 거절
    public void rejectMatch(String userId) {
        // 거절한 사용자를 대기열에 다시 등록
        addUserToWaitingList("default", userId);

        // 거절 알림
        notifyMatchRejection(userId);
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
