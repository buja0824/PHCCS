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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final RoleMapper roleMapper;
    private final RequestMatcher whitelistRequestMatcher;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            // 화이트리스트 요청은 JWT 검증 생략
            if (whitelistRequestMatcher.matches(httpRequest)) {
                log.info("화이트리스트 요청: {}", requestURI);
                chain.doFilter(httpRequest, httpResponse);
                return;
            }

            // 1. Authorization 헤더에서 Access Token 추출
            String token = extractTokenFromRequest(httpRequest);

            // 2. 토큰 검증 및 처리
            if (token != null) {
                TokenStatus status = jwtUtil.validateAccessToken(token);

                switch (status) {
                    case VALID:
                        boolean isAccessToken = jwtUtil.hasRoleClaim(token); // role 클레임 여부 확인

                        if (isAccessToken) {
                            // Access Token 처리: 인증 객체 생성
                            Long memberId = jwtUtil.extractSubject(token);
                            int roleNumber = jwtUtil.extractRole(token); // 숫자 권한 추출
                            String role = roleMapper.mapRole(roleNumber); // 권한 문자열 매핑

                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    memberId, null, Collections.singleton(new SimpleGrantedAuthority(role))
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.info("Access Token 처리 완료: memberId={}, role={}", memberId, role);
                        } else {
                            // Refresh Token 처리: 권한 없는 인증 객체 생성
                            Long memberId = jwtUtil.extractSubject(token);

                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    memberId, null, Collections.emptyList() // 권한 없이 인증 객체 생성
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            log.info("Refresh Token 처리 완료: 인증 객체 생성 (권한 없음), memberId={}", memberId);
                        }

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
            log.error("인증 필터 중 예외 발생 - URI: {}, 오류 메시지: {}", requestURI, e.getMessage(), e);
            throw e;
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // 1. Authorization 헤더에서 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        // 2. 쿠키에서 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
