package com.example.ariga_seiya_forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Column(name = "post_id")
    private Integer messageId; // どの投稿に対するコメントか

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "created_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false)
    private User user;
}