package com.hotpotatoes.potatalk.user.service;

import com.hotpotatoes.potatalk.user.dto.ProfileImageRequest;
import com.hotpotatoes.potatalk.user.dto.ProfileImageResponse;
import com.hotpotatoes.potatalk.user.type.ProfileImageType;
import com.hotpotatoes.potatalk.user.entity.User;
import com.hotpotatoes.potatalk.user.repository.UserRepository;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    // 한 줄 소개 글자수 제한 상수
    private static final int MAX_INTRODUCTION_LENGTH = 50;

    // 한줄소개 변경
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

    // 회원 탈퇴
    public void deleteUser(HttpServletRequest request) {
        // 1. 액세스 토큰 검증
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken == null || !tokenProvider.validateToken(accessToken)) {
            throw new SecurityException("유효하지 않은 토큰입니다.");
        }

        // 2. 액세스 토큰에서 사용자 ID 추출
        Long userId = Long.parseLong(tokenProvider.getSubject(accessToken));

        // 3. 데이터베이스에서 사용자 삭제
        userRepository.deleteById(userId);
    }

    // 서비스의 전체 프로필 사진 이미지 조회
    @Transactional(readOnly = true)
    public ProfileImageResponse getProfileImages(Principal principal) {
        User user = getUserFromPrincipal(principal);
        String currentImageId = user.getCurrentImageId();
        return ProfileImageResponse.of(currentImageId);
    }

    // 프로필 사진 변경
    @Transactional
    public void updateProfileImage(ProfileImageRequest request, Principal principal) {
        User user = getUserFromPrincipal(principal);

        // ProfileImageType에 존재하는 이미지id인지 확인
        ProfileImageType.fromImageId(request.getImageId());

        // imageId 저장
        user.setCurrentImageId(request.getImageId());
        userRepository.save(user);
    }

    // Principal에서 User 정보 가져오기
    private User getUserFromPrincipal(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
