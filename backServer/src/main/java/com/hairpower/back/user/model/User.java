package com.hairpower.back.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String gender;
    private String imageUrl; // S3 업로드된 이미지 URL 저장

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private com.hairpower.back.analysis.model.FaceAnalysis faceAnalysis;
}
