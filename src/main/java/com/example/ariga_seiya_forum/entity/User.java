package com.example.ariga_seiya_forum.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "branch_id", nullable = false)
    private Integer branchId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(nullable = false)
    private Integer isStopped;

    @Column(name = "created_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "branch_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "department_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private Department department;
}