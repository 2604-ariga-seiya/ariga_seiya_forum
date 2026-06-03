package com.example.ariga_seiya_forum.service;

import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.controller.form.UserUpdateForm;
import com.example.ariga_seiya_forum.entity.User;
import com.example.ariga_seiya_forum.exception.*;
import com.example.ariga_seiya_forum.repository.UserRepository;
import com.example.ariga_seiya_forum.utils.CipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<UserForm> findByAccountAndPassword(String account, String password) {
        log.info("[UserService] Initiating login authentication for account: {}.", account);

        String encryptedPassword = CipherUtil.encrypt(password);
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
            if (user.getBranch() != null) {
                userForm.setBranchName(user.getBranch().getName());
            }

            if (user.getDepartment() != null) {
                userForm.setDepartmentName(user.getDepartment().getName());
            }
            userList.add(userForm);
        }
        return userList;
    }

    public List<UserForm> findAllUsers() {

        List<User> results = userRepository.findAllByOrderByIdAsc();

        log.info("[UserService] Found {} user for display.", results.size());

        return setUserForm(results);
    }

    @Transactional
    public void changeStatus(Integer userId, Integer status) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("ユーザーが見つかりません。ID: " + userId);
        }

        user.setIsStopped(status);

        log.info("[UserService] User ID: {}'s status has been changed to {}.", userId, status);

        userRepository.save(user);
    }

    public void registerUser(UserForm userForm) {

        if(!Objects.equals(userForm.getPassword(), userForm.getPasswordConfirm())){
            log.warn("[UserService] Password and passwordConfirm do not match. account: {}", userForm.getAccount());

            throw new PasswordMismatchException("Password mismatch");
        }

        List<User> existingUser = userRepository.findByAccount(userForm.getAccount());

        if (!existingUser.isEmpty()) {
            log.warn("[UserService] Account already exists. account: {}", userForm.getAccount());

            throw new AccountDuplicateException("Account already in use");
        }

        Integer branch = userForm.getBranchId();
        Integer department = userForm.getDepartmentId();
        userForm.setIsStopped(0);

        if (branch != null && department != null) {
            if (branch == 1) {
                if (department != 1 && department != 2) {
                    log.warn("[UserService] Invalid department for HQ. departmentId: {}", department);
                    throw new InvalidDepartmentException("Invalid branch and department combination");
                }
            } else {
                if (department != 3 && department != 4) {
                    log.warn("[UserService] Invalid department for Branch. departmentId: {}", department);
                    throw new InvalidDepartmentException("Invalid branch and department combination");
                }
            }
        }
        saveUser(userForm);
    }

    /*
     * 保存処理（新規登録）
     */
    public void saveUser(UserForm reqForm) {

        log.info("[UserService] Creating new user - Account: {}", reqForm.getAccount());

        // Entityへの変換
        User saveUser = setUserEntity(reqForm);

        // DBへの保存実行
        userRepository.save(saveUser);

        // 2. 正常終了を記録
        log.info("[UserService] User save operation completed successfully.");
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private User setUserEntity(UserForm reqForm){
        log.info("[UserService] Converting Form to Entity - ID: {}", reqForm.getId());

        User user = new User();
        user.setAccount(reqForm.getAccount());
        user.setPassword(reqForm.getPassword());
        user.setBranchId(reqForm.getBranchId());
        user.setDepartmentId(reqForm.getDepartmentId());
        user.setName(reqForm.getName());
        user.setIsStopped(reqForm.getIsStopped());
        return user;
    }

    /*
     * ユーザーを1つ取得
     */
    public UserForm findUserById(Integer id) {

        log.info("[UserService] Attempting to find user by ID: {}", id);

        List<User> results = new ArrayList<>();
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            log.warn("[UserService] User not found - UserID: {}", id);
            return null;
        }

        log.info("[UserService] User successfully retrieved from database. Title: {}", user.getAccount());

        results.add(user);
        List<UserForm> userList = setUserForm(results);

        return userList.get(0);
    }

    /*
     * 保存処理（更新）
     */
    public void updateUser(UserUpdateForm userUpdateForm) {

        log.info("[UserService] Updating existing User - ID: {}, Content: {}",
                userUpdateForm.getId(), userUpdateForm.getAccount());

        boolean exists = userRepository.existsById(userUpdateForm.getId());
        if (!exists) {
            log.warn("[UserService] Update failed. User ID: {} does not exist.", userUpdateForm.getId());
            throw new IllegalArgumentException("User ID " + userUpdateForm.getId() + " not found.");
        }

        if(!Objects.equals(userUpdateForm.getPassword(), userUpdateForm.getPasswordConfirm())){
            log.warn("[UserService] Password and passwordConfirm do not match. account: {}", userUpdateForm.getAccount());

            throw new PasswordMismatchException("Password mismatch");
        }

        List<User> existingUser = userRepository.findByAccountAndIdNot(userUpdateForm.getAccount(), userUpdateForm.getId());

        if (!existingUser.isEmpty()) {
            log.warn("[UserService] Account already exists. account: {}", userUpdateForm.getAccount());

            throw new AccountDuplicateException("Account already in use");
        }

        Integer branch = userUpdateForm.getBranchId();
        Integer department = userUpdateForm.getDepartmentId();

        if (branch != null && department != null) {
            if (branch == 1) {
                if (department != 1 && department != 2) {
                    log.warn("[UserService] Invalid department for HQ. departmentId: {}", department);
                    throw new InvalidDepartmentException("Invalid branch and department combination");
                }
            } else {
                if (department != 3 && department != 4) {
                    log.warn("[UserService] Invalid department for Branch. departmentId: {}", department);
                    throw new InvalidDepartmentException("Invalid branch and department combination");
                }
            }
        }

        User updateUser = setUserEntity(userUpdateForm);

        userRepository.save(updateUser);

        log.info("[UserService] User update operation completed successfully.");
    }

    private User setUserEntity(UserUpdateForm userUpdateForm) {
        User user = new User();
        user.setId(userUpdateForm.getId());
        user.setAccount(userUpdateForm.getAccount());

        if (userUpdateForm.getPassword() == null || userUpdateForm.getPassword().isEmpty()) {
            log.info("[UserService] Password is empty. Keep current password.");

            User currentUser = userRepository.findById(userUpdateForm.getId()).orElse(null);

            if(currentUser == null){
                throw new IllegalArgumentException("User ID not found: " + userUpdateForm.getId());
            }
            user.setPassword(currentUser.getPassword());

        } else {
            user.setPassword(CipherUtil.encrypt(userUpdateForm.getPassword()));
        }

        user.setName(userUpdateForm.getName());
        user.setBranchId(userUpdateForm.getBranchId());
        user.setDepartmentId(userUpdateForm.getDepartmentId());
        user.setIsStopped(userUpdateForm.getIsStopped());

        return user;
    }
}
