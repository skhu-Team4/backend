package com.hotpotatoes.potatalk.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 1. 요청에서 JWT 토큰 추출
        String token = tokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null) {
            // 2. 리프레시 토큰인지 확인
            if (tokenProvider.validateRefreshToken(token)) {
                // 리프레시 토큰은 SecurityContext에 인증 정보를 설정하지 않고 바로 필터 체인 진행
                filterChain.doFilter(request, response);
                return;
            }

            // 3. 액세스 토큰 검증 및 블랙리스트 확인
            if (tokenProvider.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {
                // 블랙리스트에 없으면 인증 객체 생성
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. 유효하지 않은 토큰이거나, 리프레시 토큰은 인증 처리하지 않음
        filterChain.doFilter(request, response);
    }
}

