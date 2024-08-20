package practice.pra.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class GptResponse {
    private String answer;
}
