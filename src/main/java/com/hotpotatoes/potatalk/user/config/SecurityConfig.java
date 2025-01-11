package com.hotpotatoes.potatalk.user.config;

import com.hotpotatoes.potatalk.user.jwt.JwtFilter;
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
    private final TokenProvider tokenprovider;
    public SecurityConfig(TokenProvider tokenprovider) {
        this.tokenprovider = tokenprovider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)  //http 기본 인증 비활성화
                .csrf(AbstractHttpConfigurer::disable)       // jwt 기반 인증에서 csrf 보호가 불필요함 -> 비활성화
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))    //JWT 기반 인증에서는 세션을 사용하지 않으므로, 세션 생성을 하지 않도록 설정
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)     //폼 로그인과 로그아웃 기능을 비활성화합니다. 로그인은 JWT를 통해 처리
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/user/**").permitAll()  //user로 시작하는 URL은 인증없이 접근 가능 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(tokenprovider), UsernamePasswordAuthenticationFilter.class) //JwtFilter를 Spring Security의 인증 필터 체인에 추가
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }   //비밀번호를 암호화하거나 검증할 때 사용할 암호화 알고리즘을 설정
}
