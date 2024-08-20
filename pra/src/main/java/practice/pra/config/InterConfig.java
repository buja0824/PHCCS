package practice.pra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import practice.pra.web.intercepter.LoginCheckInter;

//@Configuration
public class InterConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInter())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/", "/member/add", "/board", "/pet/add", "/email", "/member/find-id");
    }
}
