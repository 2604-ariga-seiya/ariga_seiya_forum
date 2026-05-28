package com.example.ariga_seiya_forum.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "posts")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(name = "created_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private User user;
}
