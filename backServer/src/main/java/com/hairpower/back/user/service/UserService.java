package com.hairpower.back.user.service;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.s3.service.S3Service;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service; // ✅ S3 업로드 서비스 추가
    private final AiService aiService; // ✅ AI 서비스 추가

    // ✅ 사용자의 ID로 조회하는 메서드
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
    }

    // ✅ 유저 생성 (S3 업로드 후 저장)
    public User createUser(String gender, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image); // ✅ S3 업로드

        User user = new User();
        user.setGender(gender);
        user.setImageUrl(imageUrl);
        user = userRepository.save(user);
        log.info("✅ 사용자 저장 완료: userId={}", user.getUserId());

        // 3️⃣ AI 서버에 자동으로 요청 보내기
        sendUserInfoToAI(user);

        return user;
    }

    // ✅ AI 서버에 사용자 정보 전송 (유저 생성 이후)
    // ✅ AI 서버에 사용자 정보 전송 (자동 실행)
    public  void sendUserInfoToAI(User user) {
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

    }

    // ✅ AI 분석 결과를 기반으로 사용자 특징 업데이트
    public void updateUserFeaturesFromAI(Long userId) {
        log.info("📡 AI 서버에서 사용자 특징 가져오기 시작: userId={}", userId);

        try {
            // AI 서버로부터 사용자 특징을 가져오기
            List<String> userFeatures = aiService.fetchUserFeaturesFromAI(userId);

            // 유저 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

            log.info("📡 AI 서버로부터 받은 user_features: {}", userFeatures);

            // 특징 업데이트
            user.setUserFeatures(userFeatures);

            // DB에 저장
            userRepository.save(user);
            log.info("✅ userFeatures가 성공적으로 업데이트되었습니다. userId={}", userId);

        } catch (WebClientResponseException e) {
            // AI 서버 응답 오류 시, 예시값으로 대체하여 처리
            log.error("❌ AI 서버 요청 중 오류 발생: {}", e.getMessage(), e);
            List<String> defaultFeatures = Arrays.asList("세모형", "짧은 코", "긴 턱", "짧은 얼굴");
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

            user.setUserFeatures(defaultFeatures);
            userRepository.save(user);
            log.info("⚠️ AI 서버 오류로 기본값이 userFeatures에 저장되었습니다. userId={}", userId);
        }
    }
}
