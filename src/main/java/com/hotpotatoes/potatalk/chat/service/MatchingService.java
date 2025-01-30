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

    public void addUserToWaitingList(String key, String userId) {
        double randomScore = ThreadLocalRandom.current().nextDouble(0, 1); // 랜덤 점수 생성
        redisTemplate.opsForZSet().add(WAITING_LIST_PREFIX + key, userId, randomScore);
    }

    public String getRandomUserFromWaitingList(String key) {
        String redisKey = WAITING_LIST_PREFIX + key;

        Set<String> matchedUsers = redisTemplate.opsForZSet().range(redisKey, 0, 0);

        if (matchedUsers == null || matchedUsers.isEmpty()) {
            return null;
        }

        String selectedUser = matchedUsers.iterator().next();

        redisTemplate.opsForZSet().remove(redisKey, selectedUser);

        return selectedUser;
    }

    public void acceptMatch(String userId, String matchedUser) {
        String matchKey = "matched:" + userId + ":" + matchedUser;
        redisTemplate.opsForValue().set(matchKey, "MATCHED");
    }

    public void rejectMatch(String userId) {
        addUserToWaitingList("default", userId); // "default"는 대기열의 키
    }
}
