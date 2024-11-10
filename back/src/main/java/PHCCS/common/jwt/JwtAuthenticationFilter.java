package PHCCS.common.jwt;

import PHCCS.common.utility.RoleMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.RequestMatcher;


import java.io.IOException;
import java.util.Collections;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final RoleMapper roleMapper; // RoleMapper 주입
    private final RequestMatcher whitelistRequestMatcher; // 화이트리스트 매처

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // 응답 인코딩 설정
        httpResponse.setContentType("text/html; charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            // 화이트리스트 요청은 JWT 검증 생략
            if (whitelistRequestMatcher.matches(httpRequest)) {
                log.info("화이트리스트 요청: {}", requestURI);
                chain.doFilter(httpRequest, httpResponse);
                return;
            }

            // 1. Authorization 헤더에서 Access Token 추출
            String token = null;
            String authorizationHeader = httpRequest.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                log.info("Authorization 헤더에서 토큰 추출: {}", token);
            }

            // 2. 쿠키에서 Access Token 추출 (Authorization 헤더가 없는 경우)
            if (token == null) {
                if (httpRequest.getCookies() != null) {
                    for (Cookie cookie : httpRequest.getCookies()) {
                        if ("accessToken".equals(cookie.getName())) {
                            token = cookie.getValue();
                            log.info("쿠키에서 토큰 추출: {}", token);
                            break;
                        }
                    }
                }
            }

            // 3. 토큰 검증 및 처리
            if (token != null) {
                TokenStatus status = jwtUtil.validateAccessToken(token);

                switch (status) {
                    case VALID:
                        Long memberId = jwtUtil.extractSubject(token);
                        int roleNumber = jwtUtil.extractRole(token); // 숫자 권한 추출

                        // 숫자 권한을 문자열 권한으로 매핑
                        String role = roleMapper.mapRole(roleNumber);

                        // 인증 객체 생성 및 SecurityContextHolder에 설정
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                memberId, null, Collections.singleton(new SimpleGrantedAuthority(role))
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.info("SecurityContextHolder에 인증 객체 설정 완료: memberId={}, role={}", memberId, role);

                        // 다음 필터로 이동
                        chain.doFilter(httpRequest, httpResponse);
                        return;
                    case EXPIRED:
                        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        httpResponse.getWriter().write("토큰이 만료되었습니다.");
                        return;
                    case INVALID_SIGNATURE:
                        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        httpResponse.getWriter().write("서명이 잘못된 토큰입니다.");
                        return;
                    case MALFORMED:
                        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        httpResponse.getWriter().write("형식이 잘못된 토큰입니다.");
                        return;
                    case UNKNOWN_ERROR:
                    default:
                        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        httpResponse.getWriter().write("알 수 없는 오류로 인해 토큰 검증에 실패했습니다.");
                        return;
                }
            } else {
                log.warn("Authorization 헤더와 쿠키에서 토큰을 찾을 수 없습니다.");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Authorization 헤더 또는 쿠키가 누락되었습니다.");
                return;
            }

        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스와 메시지를 로그에 기록
            log.error("인증 필터 중 예외 발생 - URI: {}, 오류: {}", requestURI, e.getMessage(), e);
            throw e; // 예외를 다시 던져 상위 레벨에서 처리하도록 함
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }
}