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
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/user/signup",
                                "/api/user/login",
                                // ... 기존 permitAll 경로들 ...
                                "/api/user/refresh"
                        ).permitAll()
                        .requestMatchers(
                                "/api/lecture/my/**"  // 내 강의 관련 엔드포인트는 인증 필요
                        ).authenticated()
                        .requestMatchers(
                                "/api/lecture/**"     // 일반 강의 조회는 인증 없이 허용
                        ).permitAll()
                        .requestMatchers(
                                "/api/user/profile-images",
                                "/api/user/profile-image"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtFilter(tokenProvider, tokenBlacklist),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
