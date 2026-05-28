package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.CommentForm;
import com.example.ariga_seiya_forum.controller.form.MessageForm;
import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.service.CommentService;
import com.example.ariga_seiya_forum.service.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Controller
public class TopController {

    @Autowired
    MessageService messageService;
    @Autowired
    CommentService commentService;

    @GetMapping("/top")
    public ModelAndView view(HttpSession session){

        log.info("[TopController] Received request to display Home page.");

        ModelAndView mav = new ModelAndView();

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        mav.addObject("loginUser", loginUser);

        List<MessageForm> messageList = messageService.findAllMessages(null, null);

        List<CommentForm> commentList = commentService.findAllComments(messageList);

        // 画面遷移先を指定
        mav.setViewName("top");

        mav.addObject("messages", messageList);
        mav.addObject("comments", commentList);

        return mav;
    }

    @GetMapping("/search")
    public ModelAndView Categorize(
        @RequestParam(name = "startDate", required = false) String startStr,
        @RequestParam(name = "endDate", required = false) String endStr,
        @RequestParam(name = "category", required = false) String category){

        log.info("[TopController] Received request to search. start: {}, end: {}, category: {}", startStr, endStr, category);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/top"); // 遷移先はトップ画面

        mav.addObject("startDate", startStr);
        mav.addObject("endDate", endStr);

        try {
            List<MessageForm> messageList = messageService.findByCategorize(startStr, endStr, category);
            mav.addObject("messages", messageList);

        }catch(DateTimeParseException e){
            log.warn("[TopController] Invalid date format submitted: {}", e.getMessage());
            mav.addObject("errorMessage", "不正なパラメータです。");

            mav.addObject("messages", messageService.findAllMessages(null, null));
        }

        return mav;
    }
}