package com.hairpower.back.chat.controller;

import com.hairpower.back.chat.dto.ChatRequestDto;
import com.hairpower.back.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final UserService userService;

    // ✅ AI 분석 시작 (기본값 적용)
    @PostMapping("/start-analysis/{userId}")
    public ResponseEntity<String> startAiAnalysis(@PathVariable Long userId) {
        userService.updateUserFeaturesFromAI(userId);
        return ResponseEntity.ok("AI 분석 완료 및 userFeatures 저장됨.");
    }

    // ✅ AI 챗봇 응답 받기
    @PostMapping("/ask-ai")
    public ResponseEntity<String> askAi(@RequestBody ChatRequestDto requestDto) {
        return ResponseEntity.ok("AI 응답 예제");
    }
}
