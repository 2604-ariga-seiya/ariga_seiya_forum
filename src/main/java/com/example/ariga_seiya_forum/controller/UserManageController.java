package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.CommentForm;
import com.example.ariga_seiya_forum.controller.form.MessageForm;
import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
public class UserManageController {

    @Autowired
    UserService userService;

    /*
     * ユーザー管理画面表示
     */
    @GetMapping("/user/management")
    public ModelAndView view(HttpSession session){

        log.info("[UserManageController] Received request to display Home page.");

        ModelAndView mav = new ModelAndView();

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        mav.addObject("loginUser", loginUser);

        List<UserForm> userList = userService.findAllUsers();

        // 画面遷移先を指定
        mav.setViewName("user-management");

        mav.addObject("userList", userList);

        String bindingResultKey = org.springframework.validation.BindingResult.MODEL_KEY_PREFIX + "userForm";

        if (session.getAttribute(bindingResultKey) != null) {
            mav.addObject(bindingResultKey, session.getAttribute(bindingResultKey));
            mav.addObject("userForm", session.getAttribute("userForm"));

            // 一度画面に渡したらセッションから削除する
            session.removeAttribute(bindingResultKey);
            session.removeAttribute("userForm");
        } else {
            // 通常アクセス時は、エラー情報の入っていない綺麗な空のフォームを置く
            mav.addObject("userForm", new CommentForm());
        }

        if (session.getAttribute("errorMessage") != null) {
            mav.addObject("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage");
        }

        return mav;
    }
}
