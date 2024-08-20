package practice.pra.web.controller.gptcontroller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import practice.pra.domain.ApiId;
import practice.pra.domain.AssistantIdConst;
import practice.pra.domain.GptResponse;
import practice.pra.web.service.OpenAiService;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AssistantsController {
    private final OpenAiService service;
    private final ApiId apiId;
    private final  GptResponse gptResponse;
//    ApiId apiId = new ApiId();
//    @GetMapping("create-assistant")
//    public Mono<String> createAssistant(){
//        return service.createAssistant();
//    }
//    @GetMapping("create-assistant")   // 07-21 챗지피티 안에서 생성하고 id가지고 온다음에 상수로 정의 한거 쓸꺼임
//    public String createAssistant(){ // 07-20 나중에 싱글톤으로 만들어서 사용해야 할 듯?
//        String assistantId = service.createAssistant();
//        apiId.setAssistantId(assistantId);
//        return assistantId;
//    }


    
    @GetMapping("create-thread")    // 사용자별로 개인의 방을 만들어 주어야 할 것 같음
    public String createThread(){
        String threadId = service.createThreads();
        apiId.setThreadId(threadId);
        return threadId;
    }

    @GetMapping("create-message")    // 어떤 메시지를 보내려는지는 사용자가 보낸 사진의 결과를 담기
    public String createMessage(@RequestParam("prompt") String prompt){
        log.info("prompt = {}", prompt);
        String msgId = service.createMessage(apiId.getThreadId(), prompt);
        apiId.setMessageId(msgId);
        return msgId;
    }
//    @GetMapping("create-run")
//    public String createRun(e){
//        String message = service.createRun(apiAssistantId.getAssistantId(), apiAssistantId.getThreadId());
//        return message;
//    }
    @GetMapping("create-run")   // 실행하고
    public String createRun(HttpServletResponse response) throws InterruptedException, IOException {
        String runId = service.createRun(AssistantIdConst.ASSISTANT_ID, apiId.getThreadId());
        apiId.setRunId(runId);
//        if(!apiId.getRunId().isEmpty()){
//            response.sendRedirect("http://localhost:8080/create-result");
//        }
        return runId;
    }
    @GetMapping("create-result")     // 답변 생성 되었나?
    public Mono<String> createResult(HttpServletResponse response) throws IOException {
        Mono<String> result = service.createResult(apiId.getThreadId(), apiId.getRunId());
//        if(result != null){
//            response.sendRedirect("http://localhost:8080/show-message");
//        }
        return result;
    }

    @GetMapping("show-message") // 답변 보여줘
    public Mono<String> showMessage(){
        Mono<String> stringMono = service.showMessage(apiId.getThreadId());
//        log.info("gptResponse.getAnswer() = {}", gptResponse.getAnswer());
//        return gptResponse.getAnswer();
        return stringMono;

    }
}
