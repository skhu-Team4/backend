package com.hotpotatoes.potatalk.user.contoller;

import com.hotpotatoes.potatalk.user.dto.LoginDto;
import com.hotpotatoes.potatalk.user.dto.TokenDto;
import com.hotpotatoes.potatalk.user.dto.UserInfoDto;
import com.hotpotatoes.potatalk.user.dto.UserSignUpDto;
import com.hotpotatoes.potatalk.user.service.EmailService;
import com.hotpotatoes.potatalk.user.service.UserService;
import com.hotpotatoes.potatalk.user.service.VerificationService;
import com.hotpotatoes.potatalk.user.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import java.security.Principal;

@RestController //이 클래스가 RESTful 웹 서비스 요청을 처리하는 컨트롤러임을 나타냄
@RequestMapping("/api/user") //클래스 내의 모든 메서드가 /api/user 경로를 기준으로 동작
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) //userService 필드에 대해 final로 선언된 생성자를 자동으로 생성, 외부에서 접근 불가
public class UserController { //회원가입과 사용자 정보를 관리하는 비즈니스 로직을 포함한 서비스 클래스
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationService verificationService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        TokenDto response = userService.signUp(userSignUpDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getuser")
    public ResponseEntity<UserInfoDto> getUser(Principal principal) {
        UserInfoDto userInfoDto = userService.findByPrincipal(principal);

        return ResponseEntity.ok(userInfoDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        TokenDto response = userService.login(loginDto); // 로그인 로직 호출
        return ResponseEntity.ok(response);
    }

    // 아이디 중복 체크
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam String loginId) {
        boolean isAvailable = userService.isLoginIdAvailable(loginId);
        return ResponseEntity.ok(isAvailable);
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }
    // 이메일 인증번호 전송 API
    @PostMapping("/email")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        // 이메일 존재 확인
        Optional<User> userOptional = userService.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("이 이메일은 등록되지 않았습니다.");
        }

        // 인증번호 생성 및 저장
        String verificationCode = emailService.generateVerificationCode();
        verificationService.storeVerificationCode(email, verificationCode);

        // 이메일 전송
        emailService.sendVerificationEmail(email, verificationCode);

        return ResponseEntity.ok("인증번호가 이메일로 전송되었습니다.");
    }

    // 인증번호 검증 API (선택적으로 추가)
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = verificationService.validateVerificationCode(email, code);
        if (!isValid) {
            return ResponseEntity.badRequest().body("인증번호가 올바르지 않거나 만료되었습니다.");
        }
        return ResponseEntity.ok("인증번호가 확인되었습니다.");
    }

    // 아이디 반환 API
    @GetMapping("/id")
    public ResponseEntity<String> getIdByEmail(@RequestParam String email) {
        // 이메일로 사용자 조회
        Optional<User> userOptional = userService.findUserByEmail(email);
        // 등록되지 않은 이메일 처리
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("이 이메일은 등록되지 않았습니다.");
        }
        // 이메일이 존재하면 아이디 반환
        String loginId = userOptional.get().getLoginId();
        return ResponseEntity.ok("아이디는: " + loginId);
    }

    // 아이디 이메일 연동 확인
    @GetMapping("/verify-user")
    public ResponseEntity<Void> validateUserEmail(
            @RequestParam String loginId,
            @RequestParam String email) {
        Optional<User> userOptional = userService.findByLoginIdAndEmail(loginId, email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경 API
    @PutMapping("/password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String loginId,
            @RequestParam String newPassword) {
        try {
            userService.updatePassword(loginId, newPassword);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
