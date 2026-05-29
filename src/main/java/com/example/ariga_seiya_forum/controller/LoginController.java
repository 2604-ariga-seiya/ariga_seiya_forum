package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.LoginForm;
import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.exception.BadCredentialsException;
import com.example.ariga_seiya_forum.exception.DisabledException;
import com.example.ariga_seiya_forum.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
            BindingResult result,
            HttpSession session){

        log.info("[LoginController] Processing login request for account: {}.", loginForm.getAccount());

        ModelAndView mav = new ModelAndView();

        if(result.hasErrors()){
            log.warn("[LoginController] Login rejected due to validation errors.");
            mav.setViewName("/login");
            return mav;
        }

        try {
            // 認証処理を実行
            List<UserForm> userFormList = userService.findByAccountAndPassword(loginForm.getAccount(), loginForm.getPassword());

            UserForm loginUser = userFormList.get(0);
            session.setAttribute("loginUser", loginUser);

            log.info("[LoginController] Login successful. Saved user to session. Redirecting to home page for account: {}.", loginForm.getAccount());
            mav.setViewName("redirect:/top");

        } catch (BadCredentialsException | DisabledException e) {
            log.warn("[LoginController] Login failed for account: {}. Reason: {}", loginForm.getAccount(), e.getMessage());
            mav.addObject("errorMessage", "E0003");

            mav.setViewName("/login");
        }

        return mav;
    }

    /*
     * ログアウト処理
     */
    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {

        log.info("[LoginController] Processing logout request.");

        if (session != null) {
            session.invalidate();
            log.info("[LoginController] Session invalidated successfully. Logout complete. Redirecting to login page.");
        } else {
            // セッションが元々なかった場合（2重ログアウトやタイムアウトなど）
            log.info("[LoginController] No active session found. Already logged out. Redirecting to login page.");
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/login");

        log.info("[LoginController] Execution finished. Transitioning to login view.");
        return mav;
    }
}
