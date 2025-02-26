package com.hairpower.back.user.service;

import com.hairpower.back.s3.service.S3Service;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // ✅ 사용자 ID로 유저 조회 메서드 추가
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // ✅ 사용자 생성 (이미지 업로드 후 DB 저장)
    public User createUser(String gender, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image);

        User user = new User();
        user.setGender(gender);
        user.setImageUrl(imageUrl);

        return userRepository.save(user);
    }

    // ✅ AI 분석 결과 저장
    public void updateUserFeatures(Long userId, List<String> features) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUserFeatures(features);
            userRepository.save(user);
        }
    }
}
