package com.hotpotatoes.potatalk.user.service;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationService {

    private final Map<String, String> verificationCodes = new HashMap<>();
    private final Map<String, Long> expirationTimes = new HashMap<>();

    // 인증번호 저장 메서드
    public void storeVerificationCode(String email, String code) {
        // 기존 인증번호 제거
        verificationCodes.remove(email);
        expirationTimes.remove(email);

        verificationCodes.put(email, code);
        expirationTimes.put(email, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)); // 5분 유효
    }

    // 인증번호 검증 메서드
    public boolean validateVerificationCode(String email, String code) {

        if (!verificationCodes.containsKey(email)) {
            return false;
        }

        // 유효기간 확인
        long expirationTime = expirationTimes.get(email);
        if (System.currentTimeMillis() > expirationTime) {
            verificationCodes.remove(email);
            expirationTimes.remove(email);
            return false;
        }

        // 인증번호 확인
        return verificationCodes.get(email).equals(code);
    }
}
