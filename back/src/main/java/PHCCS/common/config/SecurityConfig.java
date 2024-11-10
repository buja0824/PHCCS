package PHCCS.common.config;

import PHCCS.common.jwt.JwtAuthenticationFilter;
import PHCCS.common.jwt.JwtUtil;
import PHCCS.common.utility.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스
    private final RoleMapper roleMapper; // RoleMapper

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // JwtAuthenticationFilter를 생성하며 화이트리스트 매처 전달
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, roleMapper, whitelistRequestMatcher());

        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT 인증 시)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelistRequestMatcher()).permitAll() // 화이트리스트
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 권한 필요
                        .anyRequest().hasAnyRole("MEMBER", "VET", "ADMIN") // 사용자와 관리자 모두 접근 가능
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 추가
                .formLogin(form -> form.disable()) // 기본 로그인 폼 비활성화
                .logout(logout -> logout.disable()); // 로그아웃 비활성화

        return http.build();
    }

    @Bean
    public RequestMatcher whitelistRequestMatcher() {
        // 화이트리스트에 해당하는 경로를 매칭하는 RequestMatcher 생성
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/"),
                new AntPathRequestMatcher("/admin"),
                new AntPathRequestMatcher("/admin/signin"),
                new AntPathRequestMatcher("/auth/signup/**"),
                new AntPathRequestMatcher("/auth/signin"),
                new AntPathRequestMatcher("/auth/refresh"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/v3/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/favicon.ico")
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 BCrypt
    }

    // 기본 UserDetailsService 비활성화
    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException("UserDetailsService is not supported.");
        };
    }
}