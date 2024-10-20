package PHCCS.common.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private static final String[] whitelist = {"/", "/admin", "/admin/**", "/auth/signup", "/auth/signin", "/auth/refresh", "/css/*", "/v3/**","/swagger-ui/**" };

    private final JwtUtil jwtUtil;

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

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);

                String authorizationHeader = httpRequest.getHeader("Authorization");

                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7);
                    TokenStatus status = jwtUtil.validateAccessToken(token); // TokenStatus 반환

                    switch (status) {
                        case VALID:
                            request.setAttribute("MemberId", jwtUtil.extractSubject(token));
                            break;
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
            throw e;
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}