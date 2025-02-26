package com.hairpower.back.user.controller;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AiService aiService;

    // ✅ 사용자 이미지 업로드 (유저 생성 및 AI 업로드 요청)
    @PostMapping("/upload-photo")
    public ResponseEntity<User> uploadPhoto(
            @RequestParam("gender") String gender,
            @RequestParam("image") MultipartFile image) throws IOException {

        User user = userService.createUser(gender, image);

        // ✅ AI에 이미지 업로드 요청 (User 객체 전달)
        String aiResponse = aiService.uploadPhotoToAI(user);
        System.out.println("AI 업로드 결과: " + aiResponse);

        return ResponseEntity.ok(user);
    }
}
