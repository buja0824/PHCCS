package PHCCS.jwt;

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

    private static final String[] whitelist = {"/", "/auth/signup", "/auth/signin", "/css/*", "/auth/me", "/auth/refresh"};

    private final JwtUtil jwtUtil;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("인증 체크 필터 시작{}", requestURI);

            if(isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);

                String authorizationHeader = httpRequest.getHeader("Authorization");

                if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7);
                    if (jwtUtil.validateToken(token)) {
                        request.setAttribute("id", jwtUtil.extractSubject(token));
                    }else {
                        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        httpResponse.getWriter().write("잘못된 토큰.");
                        // httpResponse.sendRedirect("/auth/signin?redirectURL=" + requestURI);
                        return;
                    }

                }else {
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.getWriter().write("Authorization 헤더가 누락되었거나 잘못되었습니다.");
                    // httpResponse.sendRedirect("/auth/signin?redirectURL=" + requestURI);
                    return;
                }
            }

            chain.doFilter(httpRequest, httpResponse);

        }catch (Exception e){
            throw e;

        }finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    private  boolean isLoginCheckPath(String requestURI){
        return  !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
