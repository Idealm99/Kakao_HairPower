package com.hairpower.back.chat.controller;

import com.hairpower.back.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AiService aiService;

    // 1. AI 얼굴 분석 요청
    @GetMapping("/select-story-image/{userId}")
    public ResponseEntity<String> getUserFeatures(@PathVariable Long userId) {
        return ResponseEntity.ok(String.join(", ", aiService.getUserFeatures(userId)));
    }

    // 2. AI 분석 결과 가져오기
    @GetMapping("/get-story-result/{userId}")
    public ResponseEntity<String> getAiResult(@PathVariable Long userId) {
        return ResponseEntity.ok(aiService.getStoryResult(userId));
    }

    // 3. 사용자 질문 응답
    @PostMapping("/chatbot/respond")
    public ResponseEntity<String> askAi(@RequestParam Long userId, @RequestParam String message) {
        return ResponseEntity.ok(aiService.chatbotRespond(userId, message));
    }
}
