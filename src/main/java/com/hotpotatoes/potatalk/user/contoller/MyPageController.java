package com.hotpotatoes.potatalk.user.contoller;

import com.hotpotatoes.potatalk.user.jwt.TokenBlacklist;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import com.hotpotatoes.potatalk.user.service.MyPageService;
import com.hotpotatoes.potatalk.user.service.UserRepository;
import com.hotpotatoes.potatalk.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController //이 클래스가 RESTful 웹 서비스 요청을 처리하는 컨트롤러임을 나타냄
@RequestMapping("/api/user") //클래스 내의 모든 메서드가 /api/user 경로를 기준으로 동작
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) //userService 필드에 대해 final로 선언된 생성자를 자동으로 생성, 외부에서 접근 불가


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
        // 1. 리프레시 토큰 추출
        String refreshToken = tokenProvider.resolveToken(request);

        // 2. 리프레시 토큰 검증
        if (refreshToken == null || !tokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
        }

        // 3. 블랙리스트에 추가
        tokenBlacklist.addToBlacklist(refreshToken);

        // 4. 응답 반환
        return ResponseEntity.ok("로그아웃 성공");
    }
    // 탈퇴하기
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        // 요청 보낸 사용자의 인증 정보 확인 및 삭제 로직 실행
        myPageService.deleteUser(request);
        return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
    }
}
