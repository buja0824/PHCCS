package practice.pra.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ApiId {
    private String threadId;
    private String messageId;
    private String runId;
    private String status;
}
