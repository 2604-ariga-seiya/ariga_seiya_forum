package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.LoginForm;
import com.example.ariga_seiya_forum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    UserService userService;
    /*
     * ログイン画面表示処理
     */
    @GetMapping("/login")
    public ModelAndView view(){

        log.info("[LoginController] Received request to display login page.");

        ModelAndView mav = new ModelAndView();

        // form用の空のentityを準備
        LoginForm loginForm = new LoginForm();

        // 画面遷移先を指定
        mav.setViewName("/login");

        // 準備した空のformを保管
        mav.addObject("formModel", loginForm);

        return mav;
    }

    /*
     * ログイン処理
     */
    @PostMapping("/login")
    public ModelAndView login(
            @Validated @ModelAttribute("formModel") LoginForm loginForm,
            BindingResult result){

        log.info("[LoginController] Processing login request for account: {}.", loginForm.getAccount());

        ModelAndView mav = new ModelAndView();

        if(result.hasErrors()){
            log.warn("[LoginController] Login rejected due to validation errors.");
            mav.setViewName("/login");
            return mav;
        }

        try{
            userService.findByAccountAndPassword(loginForm.getAccount(), loginForm.getPassword());
        } catch (){

        }

        // （認証処理の途中でエラーが発生した場合）
        log.warn("[LoginController] Login failed for account: {}. Invalid password.", loginForm.getAccount());

        // （ユーザーが停止状態だった場合）
        log.warn("[LoginController] Login failed for account: {}. Account is suspended.", loginForm.getAccount());

        // （ログインが成功した場合）
        log.info("[LoginController] Login successful. Redirecting to top page for account: {}.", loginForm.getAccount());

        return mav;
    }
}
