package com.example.ariga_seiya_forum.controller.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageForm {
    private Integer id;
    private String title;
    private String content;
    private String category;
    private Integer userId;
    private String name;    // ユーザー名（users.name）用
    private String account; // アカウント名（users.account）用
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
