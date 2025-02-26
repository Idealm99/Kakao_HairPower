package com.hairpower.back.user.controller;

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

    // ✅ 사용자 이미지 업로드 (S3 저장 후 URL 반환)
    @PostMapping("/upload-photo")
    public ResponseEntity<User> uploadPhoto(
            @RequestParam("gender") String gender,
            @RequestParam("image") MultipartFile image) throws IOException {

        User user = userService.createUser(gender, image);
        return ResponseEntity.ok(user);
    }

//    // ✅ 유저 생성 후 AI 서버에 정보 보내기
//    @PostMapping("/send-ai/{userId}")
//    public ResponseEntity<String> sendUserInfoToAI(@PathVariable Long userId) {
//        String aiResponse = userService.sendUserInfoToAI(userId);
//        return ResponseEntity.ok(aiResponse);
//    }
}
