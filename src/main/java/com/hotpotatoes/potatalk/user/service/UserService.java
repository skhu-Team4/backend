package com.hotpotatoes.potatalk.user.service;

import com.hotpotatoes.potatalk.user.dto.LoginDto;
import com.hotpotatoes.potatalk.user.dto.TokenDto;
import com.hotpotatoes.potatalk.user.dto.UserInfoDto;
import com.hotpotatoes.potatalk.user.dto.UserSignUpDto;
import com.hotpotatoes.potatalk.user.entity.User;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenDto signUp(UserSignUpDto signUpDto) {
        User user = userRepository.save(User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .phoneNumber(signUpDto.getPhoneNumber())
                .loginId(signUpDto.getLoginId()) // loginId 필드 추가
                .name(signUpDto.getName()) // name 필드 추가
                .introduction(signUpDto.getIntroduction())
                .build());

        String accessToken = tokenProvider.createAccessToken(user);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    @Transactional(readOnly = true)
    public UserInfoDto findByPrincipal(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserInfoDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .introduction(user.getIntroduction())
                .role(user.getRole().name())
                .build();
    }

    public TokenDto login(LoginDto loginDto) {
        // 사용자 조회
        User user = userRepository.findByLoginId(loginDto.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = tokenProvider.createAccessToken(user);

        return new TokenDto(token);
    }

    // 아이디 중복 체크
    public boolean isLoginIdAvailable(String loginId) {
        return userRepository.findByLoginId(loginId).isEmpty();
    }

    // 이메일 중복 체크
    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    // 닉네임 중복 체크
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByName(nickname).isEmpty();
    }

    // 이메일로 사용자 찾기
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 이메일과 아이디로 사용자 찾기
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

        // 변경사항 저장
        userRepository.save(user);
    }
}
