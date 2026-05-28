package com.example.ariga_seiya_forum.repository;

import com.example.ariga_seiya_forum.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
