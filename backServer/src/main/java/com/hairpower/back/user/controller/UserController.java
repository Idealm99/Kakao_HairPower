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

    @PostMapping("/upload-photo")
    public ResponseEntity<User> uploadPhoto(
            @RequestParam("gender") String gender,
            @RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(userService.createUser(gender, image));
    }
}
