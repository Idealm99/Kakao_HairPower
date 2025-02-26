package com.hairpower.back.chat.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "chatId", nullable = false)
    private Chat chat;

    private String role;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
