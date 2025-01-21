package com.hotpotatoes.potatalk.user.service;

import com.hotpotatoes.potatalk.user.dto.LoginDto;
import com.hotpotatoes.potatalk.user.dto.TokenDto;
import com.hotpotatoes.potatalk.user.dto.UserInfoDto;
import com.hotpotatoes.potatalk.user.dto.UserSignUpDto;
import com.hotpotatoes.potatalk.user.entity.User;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import com.hotpotatoes.potatalk.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public UserInfoDto signUp(UserSignUpDto signUpDto) {
        User user = userRepository.save(User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .phoneNumber(signUpDto.getPhoneNumber())
                .loginId(signUpDto.getLoginId())
                .name(signUpDto.getName())
                .introduction(signUpDto.getIntroduction())
                .build());

//        String accessToken = tokenProvider.createAccessToken(user);
//        String refreshToken = tokenProvider.createRefreshToken();
//
//        user.setRefreshToken(refreshToken); // 리프레시 토큰 저장
        userRepository.save(user);

        return UserInfoDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserInfoDto findByPrincipal(HttpServletRequest request) {
        // Authorization 헤더에서 액세스 토큰 추출
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken == null || !tokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.");
        }

        // JWT에서 subject(userId) 추출
        String subject = tokenProvider.getSubject(accessToken);
        Long userId;
        try {
            userId = Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("JWT의 subject 값이 숫자가 아닙니다: " + subject, e);
        }

        // 데이터베이스에서 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));

        return new UserInfoDto(user);
    }

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        // 사용자 조회
        User user = userRepository.findByLoginId(loginDto.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken();

        userRepository.save(user);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 아이디 중복 체크
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return userRepository.findByLoginId(loginId).isEmpty();
    }

    // 이메일 중복 체크
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    // 닉네임 중복 체크
    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByName(nickname).isEmpty();
    }

    // 이메일로 사용자 찾기
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 이메일과 아이디로 사용자 찾기
    @Transactional(readOnly = true)
    public Optional<User> findByLoginIdAndEmail(String loginId, String email) {
        return userRepository.findByLoginIdAndEmail(loginId, email);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(String loginId, String newPassword) {
        // 사용자가 존재하는지 확인
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디를 가진 사용자를 찾을 수 없습니다."));

        // 새 비밀번호 해시화 및 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }
}