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

    // 대기열에 사용자 추가
    public void addUserToWaitingList(String key, String userId) {
        double randomScore = ThreadLocalRandom.current().nextDouble(0, 1); // 랜덤 점수 생성
        redisTemplate.opsForZSet().add(WAITING_LIST_PREFIX + key, userId, randomScore);
    }

    // 랜덤으로 사용자 가져오기
    public String getRandomUserFromWaitingList(String key) {
        String redisKey = WAITING_LIST_PREFIX + key;

        // ZRANDMEMBER 호출 (결과를 List로 받음)
        List<String> randomUsers = redisTemplate.opsForZSet().randomMembers(redisKey, 1);

        if (randomUsers == null || randomUsers.isEmpty()) {
            return null;
        }

        // 첫 번째 사용자 가져오기
        String selectedUser = randomUsers.get(0);

        // 매칭 성공 시 Redis 대기열에서 제거
        redisTemplate.opsForZSet().remove(redisKey, selectedUser);

        return selectedUser;
    }

    // 대기열 크기 확인
    public long getWaitingListSize(String key) {
        return redisTemplate.opsForZSet().size(WAITING_LIST_PREFIX + key);
    }
}
