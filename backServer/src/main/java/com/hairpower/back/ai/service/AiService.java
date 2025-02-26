package com.hairpower.back.ai.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiService {
    private final WebClient webClient;
    private final UserService userService;

    // ✅ AI에 이미지 업로드 요청 (사용자가 사진 업로드할 때 호출)
    public String uploadPhotoToAI(User user) {
        return webClient.post()
                .uri("https://ecb5-35-185-153-131.ngrok-free.app/upload-photo")
                .contentType(MediaType.APPLICATION_JSON)  // ✅ Content-Type 지정
                .bodyValue(new UploadPhotoRequest(user.getUserId(), user.getGender(), user.getImageUrl()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // ✅ AI 분석 결과 받아와 userFeatures 업데이트
    public void fetchUserFeaturesFromAI(Long userId) {
        AiResponse aiResponse = webClient.get()
                .uri("https://ecb5-35-185-153-131.ngrok-free.app/select-story-image/" + userId)
                .retrieve()
                .bodyToMono(AiResponse.class)
                .block();

        if (aiResponse != null && aiResponse.getUserFeatures() != null) {
            userService.updateUserFeatures(userId, aiResponse.getUserFeatures());
        }
    }

    // ✅ AI 분석 결과 조회 (userFeatures 반환)
    public List<String> getUserFeatures(Long userId) {
        Optional<User> user = userService.findUserById(userId);
        return user.map(User::getUserFeatures).orElse(null);
    }

    // ✅ AI 분석 결과 가져오기
    public String getStoryResult(Long userId) {
        AiStoryResponse response = webClient.get()
                .uri("https://ecb5-35-185-153-131.ngrok-free.app/get-story-result/" + userId)
                .retrieve()
                .bodyToMono(AiStoryResponse.class)
                .block();

        return response != null ? response.getContent().getText() : "결과를 불러오지 못했습니다.";
    }

    // ✅ 사용자 질문을 AI 챗봇에 전달하고 응답 받기
    public String chatbotRespond(Long userId, String message) {
        return webClient.post()
                .uri("https://ecb5-35-185-153-131.ngrok-free.app/chatbot/respond")
                .contentType(MediaType.APPLICATION_JSON) // ✅ Content-Type 지정
                .bodyValue(new ChatbotRequest(userId, message))
                .retrieve()
                .bodyToMono(AiChatResponse.class)
                .map(AiChatResponse::getResponse)
                .block();
    }

    // ✅ 이미지 업로드 요청 DTO (userId -> Long 타입 유지)
    private static class UploadPhotoRequest {
        @JsonProperty("userId")
        private Long userId;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("imageUrl")
        private String imageUrl;

        public UploadPhotoRequest(Long userId, String gender, String imageUrl) {
            this.userId = userId;
            this.gender = gender;
            this.imageUrl = imageUrl;
        }
    }

    // ✅ AI 응답 DTO (userFeatures 저장)
    @Getter
    private static class AiResponse {
        @JsonProperty("user_features")
        private List<String> userFeatures;
    }

    // ✅ AI 분석 결과 DTO
    @Getter
    private static class AiStoryResponse {
        @JsonProperty("content")
        private AiStoryContent content;
    }

    @Getter
    private static class AiStoryContent {
        @JsonProperty("text")
        private String text;
    }

    // ✅ AI 챗봇 요청 DTO (userId를 Long 타입으로 변경)
    private static class ChatbotRequest {
        @JsonProperty("userId")
        private Long userId;

        @JsonProperty("message")
        private String message;

        public ChatbotRequest(Long userId, String message) {
            this.userId = userId;
            this.message = message;
        }
    }

    // ✅ AI 챗봇 응답 DTO
    @Getter
    private static class AiChatResponse {
        @JsonProperty("response")
        private String response;
    }
}
