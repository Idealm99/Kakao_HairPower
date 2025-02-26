package com.hairpower.back.face.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Face {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long faceId;

    @Column(nullable = false)
    private Long userId;

    private String faceShape;
}
