package com.hotpotatoes.potatalk.user.service;

import com.hotpotatoes.potatalk.user.entity.User;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public class MyPageService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    // 한 줄 소개 글자수 제한 상수
    private static final int MAX_INTRODUCTION_LENGTH = 50;

    @Transactional
    public void updateIntroduction(Long userId, String introduction) {

        // 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 한줄소개 유효성 검사
        if (introduction == null || introduction.trim().isEmpty()) {
            throw new IllegalArgumentException("한줄소개는 빈 값이 될 수 없습니다.");
        }
        if (introduction.length() > MAX_INTRODUCTION_LENGTH) {
            throw new IllegalArgumentException("한줄소개는 최대 " + MAX_INTRODUCTION_LENGTH + "자까지 입력할 수 있습니다.");
        }

        // 한 줄 소개 업데이트
        user.setIntroduction(introduction);
        userRepository.save(user);
    }
    public void deleteUser(HttpServletRequest request) {
        // 1. 액세스 토큰 검증
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken == null || !tokenProvider.validateToken(accessToken)) {
            throw new SecurityException("유효하지 않은 토큰입니다.");
        }

        // 2. 액세스 토큰에서 사용자 ID 추출
        Long userId = Long.parseLong(tokenProvider.getSubject(accessToken));

        // 3. 데이터베이스에서 사용자 삭제 (존재 여부 확인 없이 삭제 시도)
        userRepository.deleteById(userId);
    }
}
