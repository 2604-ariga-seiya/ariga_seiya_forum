package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserForm {
    private Integer id;
    @NotBlank(message = "{E0013}")
    @Size(min = 6, max = 20, message = "{E0014}")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "{E0014}")
    private String account;

    @NotEmpty(message = "{E0016}")
    @Size(min = 6, max = 20, message = "{E0017}")
    @Pattern(regexp = "^[\\x20-\\x7e]*$", message = "{E0017}")
    private String password;

    private String passwordConfirm;

    @NotBlank(message = "{E0019}")
    @Size(max = 10, message = "{E0020}")
    private String name;
    @NotNull(message = "{E0021}")
    private Integer branchId;
    @NotNull(message = "{E0022}")
    private Integer departmentId;
    private Integer isStopped;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String branchName;
    private String departmentName;
}