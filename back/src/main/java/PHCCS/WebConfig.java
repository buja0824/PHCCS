package PHCCS;

import PHCCS.jwt.JwtAuthenticationFilter;
import PHCCS.jwt.JwtProperties;
import PHCCS.jwt.JwtUtil;
import PHCCS.web.filter.LogFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    // CORS 설정
    // (현재 프론트, 백엔드 포트를 3030로 통일 중이여서 cops 설정이 필요 없지만 백엔드 포트를 다르게 해도 프로그램이 동작될 수 있도록 cors 설정을 미리 만들어 놓음)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 허용
                .allowedOrigins("http://localhost:3030")  // 허용할 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")  // 허용할 HTTP 메서드(추가 가능)
                .allowedHeaders("Authorization", "Content-Type")  // 허용할 헤더
                .allowCredentials(true);  // 인증 관련 헤더 허용 (예: 쿠키)
    }

    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtAuthenticationFilter(jwtUtil));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

}
