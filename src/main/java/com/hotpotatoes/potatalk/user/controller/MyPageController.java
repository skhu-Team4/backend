package com.hotpotatoes.potatalk.user.controller;

import com.hotpotatoes.potatalk.user.jwt.TokenBlacklist;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import com.hotpotatoes.potatalk.user.dto.ProfileImageRequest;
import com.hotpotatoes.potatalk.user.dto.ProfileImageResponse;
import com.hotpotatoes.potatalk.user.service.MyPageService;
import com.hotpotatoes.potatalk.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MyPageController {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TokenBlacklist tokenBlacklist;
    private final MyPageService myPageService;

    // 한줄소개 변경
    @PutMapping("/{userId}/introduction")
    public ResponseEntity<String> updateIntroduction(
            @PathVariable Long userId,
            @RequestBody Map<String, String> requestBody) {
        String introduction = requestBody.get("introduction");
        try {
            myPageService.updateIntroduction(userId, introduction);
            return ResponseEntity.ok("한 줄 소개가 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String refreshToken = tokenProvider.resolveToken(request);
        if (refreshToken == null || !tokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
        }
        tokenBlacklist.addToBlacklist(refreshToken);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 탈퇴하기
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        myPageService.deleteUser(request);
        return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
    }

    // 서비스 내 사용 가능한 프로필 이미지 조회
    @GetMapping("/profile-images")
    public ResponseEntity<ProfileImageResponse> getProfileImages(Principal principal) {
        ProfileImageResponse response = myPageService.getProfileImages(principal);
        return ResponseEntity.ok(response);
    }

    // 프로필 이미지 변경
    @PutMapping("/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @RequestBody ProfileImageRequest request,
            Principal principal) {
        myPageService.updateProfileImage(request, principal);
        return ResponseEntity.ok().build();
    }
}