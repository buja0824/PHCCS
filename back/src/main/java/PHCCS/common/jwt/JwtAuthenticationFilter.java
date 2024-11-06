package PHCCS.common.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private static final String[] whitelist = {
            "/",
            "/auth/signup/**",
            "/auth/signin",
            "/auth/refresh",
            "/css/**",
            "/v3/**",
            "/swagger-ui/**",
            "/admin/**",
            "/favicon.ico"
    };

    private final JwtUtil jwtUtil;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // 응답 인코딩 설정
        httpResponse.setContentType("text/html; charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");

        try {
            log.info("인증 체크 필터 시작{}", requestURI);

            // 화이트리스트 경로 여부 확인
            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);

                String authorizationHeader = httpRequest.getHeader("Authorization");

                // 토큰 확인 및 인증 처리
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7);
                    TokenStatus status = jwtUtil.validateAccessToken(token);

                    switch (status) {
                        case VALID:
                            request.setAttribute("MemberId", jwtUtil.extractSubject(token));
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
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.getWriter().write("Authorization 헤더가 누락되었거나 잘못되었습니다.");
                    return;
                }
            }

            chain.doFilter(httpRequest, httpResponse);

        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스와 메시지를 로그에 기록
            log.error("인증 체크 필터 중 오류 발생 - URI: {}, 오류: {}", requestURI, e.getMessage(), e);
            throw e; // 예외를 다시 던져 상위 레벨에서 처리하도록 함
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    private boolean isLoginCheckPath(String requestURI) {
        try {
            for (String pattern : whitelist) {
                boolean match = antPathMatcher.match(pattern, requestURI);
                if (match) {
                    return false; // 화이트리스트에 포함된 경로는 인증 체크 제외
                }
            }
        } catch (Exception e) {
            log.error("Error occurred during pattern matching: requestURI={}, error={}", requestURI, e.getMessage(), e);
        }
        return true;
    }
}
