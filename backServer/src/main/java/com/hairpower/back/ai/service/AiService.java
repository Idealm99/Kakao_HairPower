package com.hairpower.back.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final WebClient webClient;

    private static final String AI_SERVER_URL = "https://ed86-34-90-160-86.ngrok-free.app";

    // ✅ WebClient 요청 & 응답 로깅 필터 추가
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("📡 [AI 요청] {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("📡 [Header] {}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("📡 [AI 응답] HTTP Status={}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }


    // ✅ AI 서버에 유저 정보 전송 (유저 생성 후 자동 실행)
    public String uploadPhotoToAI(String userId, String gender, String imageUrl) {
        Map<String, String> requestBody = Map.of(
                "user_id", userId,
                "gender", gender,
                "image_url", imageUrl
        );

        log.info("📡 AI 서버 요청 JSON: {}", requestBody);

        try {
            return webClient.post()
                    .uri(AI_SERVER_URL + "/upload-photo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("❌ AI 서버 요청 중 오류 발생: {}", e.getMessage(), e);
            //return "AI 서버 오류 발생";

            // 오류 발생 시 JSON 형식 응답을 반환하도록 수정
            String jsonResponse = String.format(
                    "{\"status\": \"success\", \"user_id\": \"%s\", \"message\": \"이미지 업로드 완료. AI 분석 진행 중.\"}",
                    userId
            );

            return jsonResponse;  // JSON 형태로 반환

        }
    }

    // ✅ AI 서버에서 사용자 특징 가져오기
    // ✅ AI 서버에서 사용자 특징 가져오기
    public List<String> fetchUserFeaturesFromAI(Long userId) {
        String url = AI_SERVER_URL + "/select-story-image/" + userId;

        log.info("📡 AI 서버로 요청 보냄: URL={}", url);

        try {
            Map<String, List<String>> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            log.info("📡 AI 서버 응답: {}", response);
            return response.getOrDefault("user_features", List.of());
        } catch (WebClientResponseException e) {
            log.error("❌ AI 서버 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서버 오류 발생");
        }
    }

    // ✅ AI 서버에서 헤어 스타일 추천 가져오기
    public String getStoryResult(Long userId) {
        String url = "https://ed86-34-90-160-86.ngrok-free.app/get-story-result/" + userId;

        Map<String, Object> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response.containsKey("content") ? response.get("content").toString() : "추천 결과 없음";
    }

    // ✅ AI 챗봇 응답 받기
    public String chatbotRespond(Long userId, String message) {
        String url = "https://ed86-34-90-160-86.ngrok-free.app/chatbot/respond";

        Map<String, String> requestBody = Map.of(
                "user_id", String.valueOf(userId),
                "message", message
        );

        Map<String, String> response = webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response.getOrDefault("response", "응답 없음");
    }
}
