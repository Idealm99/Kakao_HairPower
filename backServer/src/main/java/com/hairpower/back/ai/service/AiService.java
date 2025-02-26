package com.hairpower.back.ai.service;

import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private static final String AI_SERVER_BASE_URL = "http://ai-server-url";

    // 1. AI에 이미지 업로드 요청
    public String uploadPhotoToAI(User user) {
        String url = AI_SERVER_BASE_URL + "/upload-photo";
        Map<String, String> requestBody = Map.of(
                "user_id", String.valueOf(user.getUserId()),
                "gender", user.getGender(),
                "image_string", user.getImageUrl()
        );
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("message"))
                .block();
    }

    // 2. AI 얼굴 분석 요청
    public List<String> getUserFeatures(Long userId) {
        String url = AI_SERVER_BASE_URL + "/select-story-image/" + userId;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (List<String>) response.get("user_features"))
                .block();
    }

    // 3. AI 분석 결과 가져오기 (DB 저장 없이 바로 반환)
    public String getStoryResult(Long userId) {
        String url = AI_SERVER_BASE_URL + "/get-story-result/" + userId;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("content"))
                .block();
    }

    // 4. 사용자 질문 → AI 질문 → 응답 반환
    public String chatbotRespond(Long userId, String message) {
        String url = AI_SERVER_BASE_URL + "/chatbot/respond";
        Map<String, String> requestBody = Map.of(
                "user_id", String.valueOf(userId),
                "message", message
        );
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("response"))
                .block();
    }
}
