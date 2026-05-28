package com.example.ariga_seiya_forum.repository;

import com.example.ariga_seiya_forum.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Message> findAllByCreatedDateBetweenAndCategory(LocalDateTime start, LocalDateTime end, Integer categoryId, Pageable pageable);
}

