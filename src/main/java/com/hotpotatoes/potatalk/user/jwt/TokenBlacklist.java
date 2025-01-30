package com.hotpotatoes.potatalk.user.jwt;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TokenBlacklist {
    private final Set<String> blacklist = new HashSet<>();

    // 블랙리스트에 토큰 추가
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    // 블랙리스트에 해당 토큰이 있는지 확인
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}