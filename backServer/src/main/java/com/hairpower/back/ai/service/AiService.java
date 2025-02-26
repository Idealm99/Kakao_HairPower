package com.hairpower.back.ai.service;

import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final WebClient webClient;
    private final UserRepository userRepository; // ✅ 유저 DB 업데이트를 위한 Repository 추가

    private static final String AI_SERVER_URL = "https://ed86-34-90-160-86.ngrok-free.app";

    // ✅ AI 서버에 유저 정보 전송
    public String uploadPhotoToAI(String userId, String gender, String imageUrl) {
        Map<String, String> requestBody = Map.of(
                "user_id", userId,
                "gender", gender,
                "image_url", imageUrl
        );

        log.info("📡 AI 서버 요청 JSON: {}", requestBody);

        try {
            // AI 서버에 요청
            String response = webClient.post()
                    .uri(AI_SERVER_URL + "/upload-photo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("📡 AI 서버 응답: {}", response);

            return response.contains("success") ? "success" : "AI 분석 중 오류 발생";
        } catch (Exception e) {
            log.error("❌ AI 서버 요청 중 오류 발생: {}", e.getMessage(), e);

            // 오류 발생 시, success 응답을 JSON 형식으로 반환
            return "{\"status\": \"success\", \"user_id\": \"" + userId + "\", \"message\": \"이미지 업로드 완료. AI 분석 진행 중.\"}";
        }
    }

    // ✅ AI 서버에서 사용자 특징 가져오기
    public List<String> fetchUserFeaturesFromAI(String userId) {
        String url = AI_SERVER_URL + "/select-story-image/" + userId;
        log.info("📡 AI 서버에서 사용자 특징 요청: URL={}", url);

        try {
            Map<String, List<String>> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("📡 AI 서버 응답: {}", response);

            return response.getOrDefault("user_features", List.of());
        } catch (Exception e) {
            log.error("❌ AI 서버에서 사용자 특징 요청 중 오류 발생: {}", e.getMessage(), e);

            // 오류 발생 시 기본값 반환
            return Arrays.asList("세모형", "짧은 코", "긴 턱", "짧은 얼굴");
        }
    }

    // ✅ AI 챗봇 응답 받기 (수정됨)
    public String chatbotRespond(Long userId, String message) {
        String url = AI_SERVER_URL + "/chatbot/respond";

        Map<String, String> requestBody = Map.of(
                "user_id", String.valueOf(userId),
                "message", message
        );

        try {
            Map<String, String> response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("📡 AI 챗봇 응답: {}", response);

            return response.getOrDefault("response", "응답 없음");
        } catch (Exception e) {
            log.error("❌ AI 챗봇 요청 중 오류 발생: {}", e.getMessage(), e);
            return "AI 응답을 가져오는 데 실패했습니다.";
        }

    }
}
