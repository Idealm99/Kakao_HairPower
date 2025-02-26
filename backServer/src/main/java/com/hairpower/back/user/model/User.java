package com.hairpower.back.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String gender;
    private String imageUrl;

    // ✅ 변경: 불변 리스트 -> 변경 가능한 리스트(ArrayList)
    @ElementCollection
    @CollectionTable(name = "user_features", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "feature")
    private List<String> userFeatures = new ArrayList<>();

    public void setUserFeatures(List<String> features) {
        this.userFeatures.clear();  // 기존 값 제거
        this.userFeatures.addAll(features);  // 새 값 추가
    }

}
