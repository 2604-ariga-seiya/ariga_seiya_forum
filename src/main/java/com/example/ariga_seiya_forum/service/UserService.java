package com.example.ariga_seiya_forum.service;

import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.entity.User;
import com.example.ariga_seiya_forum.exception.BadCredentialsException;
import com.example.ariga_seiya_forum.exception.DisabledException;
import com.example.ariga_seiya_forum.repository.UserRepository;
import com.example.ariga_seiya_forum.utils.CipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<UserForm> findByAccountAndPassword(String account, String password) {
        log.info("[UserService] Initiating login authentication for account: {}.", account);

        String encryptedPassword = CipherUtil.encrypt(password);
        //System.out.println(encryptedPassword);
        List<User> results = userRepository.findAllByAccountAndPassword(account, encryptedPassword);

        log.info("[UserService] Database search completed. Found {} records.", results.size());

        // 1. アカウント名またはパスワードが不一致
        if (results.isEmpty()) {
            log.warn("[UserService] Authentication failed: Account not found or password mismatch for account: {}.", account);
            throw new BadCredentialsException("Invalid account or password.");
        }

        List<UserForm> userFormList = setUserForm(results);

        // 2. アカウント停止状態
        if (userFormList.get(0).getIsStopped() == 1) {
            log.warn("[UserService] Authentication failed: Account is suspended for account: {}.", account);
            throw new DisabledException("Account is suspended.");
        }

        log.info("[UserService] Authentication successful for account: {}.", account);
        return userFormList;
    }

    private List<UserForm> setUserForm(List<User> results) {
        log.info("[UserService] Converting Entity to Form - Count: {} items.", results.size());
        List<UserForm> userList = new ArrayList<>();

        for (User user : results) {
            UserForm userForm = new UserForm();
            userForm.setId(user.getId());
            userForm.setAccount(user.getAccount());
            userForm.setPassword(user.getPassword());
            userForm.setName(user.getName());
            userForm.setBranchId(user.getBranchId());
            userForm.setDepartmentId(user.getDepartmentId());
            userForm.setIsStopped(user.getIsStopped());
            userForm.setCreatedDate(user.getCreatedDate());
            userForm.setUpdatedDate(user.getUpdatedDate());
            userList.add(userForm);
        }
        return userList;
    }
}
