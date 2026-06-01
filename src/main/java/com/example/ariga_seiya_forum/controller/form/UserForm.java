package com.example.ariga_seiya_forum.controller.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserForm {
    private Integer id;
    private String account;
    private String password;
    private String name;
    private Integer branchId;
    private Integer departmentId;
    private Integer isStopped;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String branchName;
    private String departmentName;
}