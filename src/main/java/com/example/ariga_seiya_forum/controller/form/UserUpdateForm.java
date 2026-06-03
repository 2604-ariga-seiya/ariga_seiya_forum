package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateForm {

    private Integer id;

    @NotBlank(message = "{E0019}")
    @Size(max = 10, message = "{E0020}")
    private String name;

    @NotBlank(message = "{E0013}")
    @Size(min = 6, max = 20, message = "{E0014}")
    private String account;

    @Size(max = 20, message = "{E0017}")
    private String password;

    private String passwordConfirm;

    private Integer branchId;
    private Integer departmentId;
    private Integer isStopped;
}
