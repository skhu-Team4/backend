package com.hotpotatoes.potatalk.user.config;

import com.hotpotatoes.potatalk.user.jwt.JwtFilter;
import com.hotpotatoes.potatalk.user.jwt.TokenBlacklist;
import com.hotpotatoes.potatalk.user.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;

    public SecurityConfig(TokenProvider tokenProvider, TokenBlacklist tokenBlacklist) {
        this.tokenProvider = tokenProvider;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)  // http 기본 인증 비활성화
                .csrf(AbstractHttpConfigurer::disable)       // csrf 비활성화
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))    // 세션 비활성화
                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 비활성화
                .logout(AbstractHttpConfigurer::disable)     // 로그아웃 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/user/signup",
                        "/api/user/login",
                        "/api/user/check-id",
                        "/api/user/check-email",
                        "/api/user/check-nickname",
                        "/api/user/email",
                        "/api/user/verify-email",
                        "/api/user/id",
                        "/api/user/verify-user",
                        "/api/user/password",
                        "/api/user/refresh"
                ).permitAll()
                .anyRequest().authenticated()  // 위에 명시되지 않은 모든 요청은 인증 필요 //

                )
                .addFilterBefore(new JwtFilter(tokenProvider, tokenBlacklist), UsernamePasswordAuthenticationFilter.class); // JwtFilter 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}


