package PHCCS.common.remoteserver;

import PHCCS.common.config.WebConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIImageTester {

    private final WebConfig webConfig;

    public void sendImage(String dir){
        webConfig.aiImageServer().get()
                .uri("/{dir}", dir)
    }

}
