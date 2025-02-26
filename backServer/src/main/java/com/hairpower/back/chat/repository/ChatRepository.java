package com.hairpower.back.chat.repository;

import com.hairpower.back.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}