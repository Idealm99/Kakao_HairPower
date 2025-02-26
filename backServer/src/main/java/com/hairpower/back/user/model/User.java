package com.hairpower.back.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String gender;

    private String imageUrl; // S3 이미지 URL

    @ElementCollection
    private List<String> userFeatures; // AI 얼굴 분석 결과 저장
}