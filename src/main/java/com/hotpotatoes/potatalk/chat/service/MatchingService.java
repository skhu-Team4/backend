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
        // 이미 대기열에 있는지 확인
        Boolean userExists = redisTemplate.opsForZSet().score(WAITING_LIST_PREFIX + key, userId) != null;
        if (userExists) {
            return; // 이미 대기열에 존재하면 추가하지 않음
        }

        // 랜덤 점수 생성하여 대기열에 추가
        double randomScore = ThreadLocalRandom.current().nextDouble(0, 1);
        redisTemplate.opsForZSet().add(WAITING_LIST_PREFIX + key, userId, randomScore);
    }

    // 대기열에서 랜덤으로 두 사용자 가져오기
    public List<String> getRandomUsersFromWaitingList(String key) {
        String redisKey = WAITING_LIST_PREFIX + key;

        // ZRANDMEMBER 호출 (결과를 List로 받음) - 두 명을 가져옵니다.
        List<String> randomUsers = redisTemplate.opsForZSet().randomMembers(redisKey, 2);

        if (randomUsers == null || randomUsers.size() < 2) {
            return null;  // 두 명 이상의 사용자가 없으면 null 반환
        }

        // 매칭된 두 사용자 제거
        redisTemplate.opsForZSet().remove(redisKey, randomUsers.get(0), randomUsers.get(1));

        // 두 유저 반환
        return randomUsers;
    }

    // 대기열 크기 확인
    public long getWaitingListSize(String key) {
        return redisTemplate.opsForZSet().size(WAITING_LIST_PREFIX + key);
    }
}
