package practice.pra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:api.properties")
public class AppConfig {
    // 추가적인 설정이 필요하다면 여기에 작성할 수 있습니다.
}