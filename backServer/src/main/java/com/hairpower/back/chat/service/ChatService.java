package com.hairpower.back.chat.service;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.chat.dto.ChatRequestDto;
import com.hairpower.back.chat.dto.ChatResponseDto;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final AiService aiService;
    private final UserRepository userRepository;

    // 사용자 질문을 AI에 전달하고 응답을 반환하는 메서드
    public ChatResponseDto chatWithAI(ChatRequestDto requestDto) {
        Long userId;  // userId를 Long 타입으로 변환

        try {
            userId = Long.parseLong(requestDto.getUserId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 userId 형식입니다.");
        }

        String message = requestDto.getMessage();

        // 사용자 정보 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // AI와 통신하여 응답 받기
        String aiResponse = aiService.chatbotRespond(userId, message);

        // AI 응답을 DTO로 변환하여 반환
        return new ChatResponseDto(userId.toString(), aiResponse);
    }
}
