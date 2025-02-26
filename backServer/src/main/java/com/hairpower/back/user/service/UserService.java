package com.hairpower.back.user.service;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.s3.service.S3Service;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final AiService aiService;

    // ✅ 유저 생성 (S3 업로드 후 저장)
    public User createUser(String gender, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image); // ✅ S3 업로드

        User user = new User();
        user.setGender(gender);
        user.setImageUrl(imageUrl);
        user = userRepository.save(user);
        log.info("✅ 사용자 저장 완료: userId={}", user.getUserId());

        // ✅ AI 서버에 사용자 정보 전송
        sendUserInfoToAI(user);

        return user;
    }

    // ✅ AI 서버에 사용자 정보 전송
    public void sendUserInfoToAI(User user) {
        log.info("📡 AI 서버 전송 시작... userId={}", user.getUserId());

        try {
            String aiResponse = aiService.uploadPhotoToAI(
                    user.getUserId().toString(),
                    user.getGender(),
                    user.getImageUrl()
            );
            log.info("📡 AI 서버 응답: {}", aiResponse);
        } catch (Exception e) {
            log.error("❌ AI 서버 요청 실패: {}", e.getMessage(), e);
        }

        // ✅ AI 서버에서 사용자 특징 가져오기 실행 (성공/실패 여부 상관없이 실행)
        updateUserFeaturesFromAI(user.getUserId());
    }

    // ✅ AI 분석 결과를 기반으로 사용자 특징 업데이트
    public void updateUserFeaturesFromAI(Long userId) {
        log.info("📡 AI 서버에서 사용자 특징 가져오기 시작: userId={}", userId);

        List<String> userFeatures = aiService.fetchUserFeaturesFromAI(userId.toString());

        // 로그로 확인
        log.info("📡 AI 서버로부터 받은 user_features: {}", userFeatures);

        // 유저 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("❌ 유효하지 않은 사용자 ID: {}", userId);
            return;
        }

        User user = userOptional.get();

        // ✅ userFeatures가 비어 있으면 기본값 설정
        if (userFeatures == null || userFeatures.isEmpty()) {
            userFeatures = Arrays.asList("세모형", "짧은 코", "긴 턱", "짧은 얼굴");
            log.info("⚠️ AI 서버 오류로 기본값을 userFeatures에 저장합니다.");
        }

        // 특징 업데이트
        user.setUserFeatures(userFeatures);
        userRepository.save(user);
        log.info("✅ userFeatures가 성공적으로 업데이트되었습니다. userId={}, userFeatures={}", userId, userFeatures);
    }
}
