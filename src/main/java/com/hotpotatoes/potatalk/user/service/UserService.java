package com.hotpotatoes.potatalk.user.service;

import com.hotpotatoes.potatalk.user.dto.LoginDto;
import com.hotpotatoes.potatalk.user.dto.TokenDto;
import com.hotpotatoes.potatalk.user.dto.UserInfoDto;
import com.hotpotatoes.potatalk.user.dto.UserSignUpDto;
import com.hotpotatoes.potatalk.user.entity.User;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import com.hotpotatoes.potatalk.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenDto signUp(UserSignUpDto signUpDto) {
        User user = userRepository.save(User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .loginId(signUpDto.getLoginId())
                .introduction(signUpDto.getIntroduction())
                .build());

        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken();

        user.setRefreshToken(refreshToken); // 리프레시 토큰 저장
        userRepository.save(user);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    @Transactional(readOnly = true)
    public UserInfoDto findByPrincipal(HttpServletRequest request) {
        // Authorization 헤더에서 액세스 토큰 추출
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken == null || !tokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Long userId = Long.parseLong(tokenProvider.getSubject(accessToken));

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // UserInfoDto로 변환하여 반환
        return UserInfoDto.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .introduction(user.getIntroduction())
                .currentImageId(user.getCurrentImageId())
                .role(user.getRole().name())
                .build();
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

        user.setRefreshToken(refreshToken); // 리프레시 토큰 갱신
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

        // 비밀번호 길이 검증
        if (newPassword.length() < 8 || newPassword.length() > 20) {
            throw new ValidationException("비밀번호는 8자 이상, 20자 이하이어야 합니다.");
        }

        // 비밀번호 패턴 검증 (알파벳 + 숫자 포함, 특수문자 & 한글 금지)
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?!.*[ㄱ-ㅎ가-힣])[A-Za-z\\d]+$";
        if (!Pattern.matches(passwordPattern, newPassword)) {
            throw new ValidationException("비밀번호는 최소 하나의 알파벳과 숫자를 포함해야 하며, 특수문자와 한글은 사용할 수 없습니다.");
        }

        // 새 비밀번호 해시화 및 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

}
