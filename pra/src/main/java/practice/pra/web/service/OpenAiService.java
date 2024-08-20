package practice.pra.web.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import practice.pra.domain.GptResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OpenAiService {
    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final GptResponse gptResponse;

    public OpenAiService(WebClient webClient, @Value("${openai.api-key}") String apiKey, ObjectMapper objectMapper, GptResponse gptResponse) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.gptResponse = gptResponse;
    }
//
//    public Mono<String> createAssistant() {
//        Map<String, Object> requestPayload = new HashMap<>();
//        requestPayload.put("instructions", "You are a PHD of Computer science");
//        requestPayload.put("name", "CS Ph");
//        requestPayload.put("model", "gpt-3.5-turbo");
//
//        Mono<String> authorization = webClient.post()
//                .uri("/assistants")
//                .header("Authorization", "Bearer " + apiKey)
//                .header("OpenAI-Beta", "assistants=v2")
//                .bodyValue(requestPayload)
//                .retrieve()
//                .bodyToMono(String.class)
//                .onErrorResume(WebClientResponseException.class, ex -> {
//                    return Mono.error(new RuntimeException("Failed to create assistant: " + ex.getResponseBodyAsString()));
//                });
//        String id = exId(authorization);
//        log.info("id = {}", id);
//        return authorization;
//    }
    public String createAssistant() {
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("instructions", "당신은 백종원 입니다 백종원처럼 설명해주세요");
        requestPayload.put("name", "chef");
        requestPayload.put("model", "gpt-3.5-turbo");

        Mono<String> authorization = apiCall("/assistants", "Failed to create assistant: ", requestPayload);
        String id = exId(authorization);
        log.info("assistantId = {}", id);
        return id;
    }
    public String createThreads() {
        Mono<String> authorization = apiCall("/threads", "스레드 생성 실패 : ");
        String threadId = exId(authorization);
        log.info("threadId = {}", threadId);
        return threadId;
    }
    public String createMessage(String threadId, String prompt) {
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("role", "user");
        requestPayload.put("content", prompt);
        Mono<String> authorization = apiCall("/threads/" + threadId + "/messages", "messaqe 생성 실패 : " , requestPayload);
        String msgId = exId(authorization);
        log.info("msgId = {}", msgId);

        return msgId;
    }

    public String createRun(String assistantId, String threadId) throws InterruptedException {
        log.info("런 시작");
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("assistant_id", assistantId);
        Mono<String> authorization = apiCall("/threads/" + threadId + "/runs", "run 실패 : " , requestPayload);
        log.info("런 종료, 상태 추출");
        log.info("run 상태 = {}", authorization.subscribe());
        String runId = exId(authorization);
        log.info("runId = {}", runId);
        Thread.sleep(5000);
//        String status = exStatus(authorization); //queued
//        log.info("status = {}", status);
//        while(!status.equals("queued")) {
//            Mono<String> retrieve = apiCall("threads/" + threadId + "/runs/" + runId, "run 상태 확인 실패");
//            status = exStatus(retrieve);
//            log.info("status = {}", status);
//            if(status.equals("completed")) break;
//            Thread.sleep(5000);
//        }
//        return runId;
        return runId;
    }

    public Mono<String> createResult(String threadId, String runId){
        log.info("결과 확인 시작");
        log.info("threadId = {}", threadId);
        log.info("runId = {}", runId);
        Mono<String> authorization = apiCall("/threads/" + threadId + "/runs/" + runId, "run 상태 확인 실패");
        log.info("result 상태 = {}", authorization.subscribe());
        return authorization;
    }

    public Mono<String> showMessage(String threadId){
        log.info("메시지 확인 시작");
        log.info("threadId = {}", threadId);
        Mono<String> authorization = webClient.get()
                .uri("/threads/" + threadId + "/messages")
                .header("Authorization", "Bearer " + apiKey)
                .header("OpenAI-Beta", "assistants=v2")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    return Mono.error(new RuntimeException("msg 상태  실패" + ex.getResponseBodyAsString()));
                });
        log.info("message 상태 = {}", authorization.subscribe());

        authorization.subscribe(response -> {
            try {
                JsonNode jsonResponse = objectMapper.readTree(response);
                log.info("authorization = {}", jsonResponse);
                JsonNode dataArray = jsonResponse.get("data");
                log.info("dataArray = {}", dataArray);
                boolean found = false;
                for (JsonNode dataNode : dataArray) {
                    JsonNode contentArray = dataNode.get("content");
                    log.info("contentArray = {}", contentArray);
                    for (JsonNode contentNode : contentArray) {
                        JsonNode textNode = contentNode.get("text");
                        log.info("textNode = {}", textNode);
                        if(textNode != null){
                            String valueMsg = textNode.get("value").asText();
                            log.info("valueMsg = {}", valueMsg);
                            gptResponse.setAnswer(valueMsg);
                            found = true;
                            break;

                        }
                    }
                    if(found)break;

                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        },
                error -> log.error("Error"));
        return authorization;
    }

    public Mono<String> apiCall(String uri, String errorMsg){
        log.info("uri = {}", uri);
        return webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + apiKey)
                .header("OpenAI-Beta", "assistants=v2")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    return Mono.error(new RuntimeException(errorMsg + ex.getResponseBodyAsString()));
                });
    }
    public Mono<String> apiCall(String uri, String errorMsg, Map<String, Object> requestPayload){
        return webClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + apiKey)
                .header("OpenAI-Beta", "assistants=v2")
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    return Mono.error(new RuntimeException(errorMsg + ex.getResponseBodyAsString()));
                });
    }
    public String exId(Mono<String> jsonMono) {
        // Mono<String>에서 JSON 파싱 후 id 필드 값 추출
//        String id = jsonMono.map(jsonString -> {
//            try {
//                // ObjectMapper를 사용하여 JSON 파싱
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode jsonNode = mapper.readTree(jsonString);
//
//                // id 필드의 값을 꺼내기
//                String idValue = jsonNode.get("id").asText();
//                return idValue;
//            } catch (Exception e) {
//                // 예외 처리
//                e.printStackTrace();
//                return null;
//            }
//        }).block(); // block() 메서드로 Mono를 동기적으로 실행하여 결과값 얻기
//        return id;
        String id = jsonMono.flatMap(jsonString -> {
            try {
                // ObjectMapper를 사용하여 JSON 파싱
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(jsonString);

                // id 필드의 값을 꺼내기
                String idValue = jsonNode.get("id").asText();
                return Mono.just(idValue);
            } catch (Exception e) {
                // 예외 처리
                e.printStackTrace();
                return Mono.empty();
            }
        }).block(); // block() 메서드로 Mono를 동기적으로 실행하여 결과값 얻기
        return id;
    }

    public String exStatus(Mono<String> jsonMono) {
//        // Mono<String>에서 JSON 파싱 후 status 필드 값 추출
//        String status = jsonMono.map(jsonString -> {
//            try {
//                // ObjectMapper를 사용하여 JSON 파싱
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode jsonNode = mapper.readTree(jsonString);
//
//                // status 필드의 값을 꺼내기
//                String statusValue = jsonNode.get("status").asText();
//                return statusValue;
//            } catch (Exception e) {
//                // 예외 처리
//                e.printStackTrace();
//                return null;
//            }
//        }).block(); // block() 메서드로 Mono를 동기적으로 실행하여 결과값 얻기
//        return status;

        String status = jsonMono.flatMap(jsonString -> {
            try {
                // ObjectMapper를 사용하여 JSON 파싱
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(jsonString);

                // id 필드의 값을 꺼내기
                String statusValue = jsonNode.get("status").asText();
                return Mono.just(statusValue);
            } catch (Exception e) {
                // 예외 처리
                e.printStackTrace();
                return Mono.empty();
            }
        }).block(); // block() 메서드로 Mono를 동기적으로 실행하여 결과값 얻기
        return status;
    }
}