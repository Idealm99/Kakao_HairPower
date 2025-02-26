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

    public User createUser(String gender, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image);
        User user = User.builder()
                .gender(gender)
                .imageUrl(imageUrl)
                .build();
        return userRepository.save(user);
    }

    public void updateUserFeatures(Long userId, List<String> features) {
        Optional<User> user = userRepository.findByUserId(userId);
        user.ifPresent(u -> {
            u.setUserFeatures(features);
            userRepository.save(u);
        });
    }
}
