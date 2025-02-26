package com.hairpower.back.chat.controller;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.chat.dto.ChatRequestDto;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AiService aiService;
    private final UserService userService;

    // ✅ AI 분석 시작 (이미 업로드된 사진을 AI에 전달)
    @PostMapping("/start-analysis/{userId}")
    public ResponseEntity<String> startAiAnalysis(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않은 사용자 ID입니다.");
        }

        String response = aiService.uploadPhotoToAI(userOptional.get());
        return ResponseEntity.ok(response);
    }

    // ✅ AI로부터 사용자 특징 받아서 저장
    @GetMapping("/fetch-user-features/{userId}")
    public ResponseEntity<List<String>> fetchUserFeatures(@PathVariable Long userId) {
        aiService.fetchUserFeaturesFromAI(userId);
        List<String> features = aiService.getUserFeatures(userId);
        return ResponseEntity.ok(features);
    }

    // ✅ AI 분석 결과 확인
    @GetMapping("/get-ai-result/{userId}")
    public ResponseEntity<String> getAiResult(@PathVariable Long userId) {
        String response = aiService.getStoryResult(userId);
        return ResponseEntity.ok(response);
    }

    // ✅ 사용자 질문 → AI 질문 → 응답 반환
    @PostMapping("/ask-ai")
    public ResponseEntity<String> askAi(@RequestBody ChatRequestDto requestDto) {
        Long userId = Long.parseLong(requestDto.getUserId()); // String -> Long 변환
        String response = aiService.chatbotRespond(userId, requestDto.getMessage());
        return ResponseEntity.ok(response);
    }
}
