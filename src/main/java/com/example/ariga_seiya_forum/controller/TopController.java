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

        List<MessageForm> messageList = messageService.findAllMessages();

        List<CommentForm> commentList = commentService.findAllComments(messageList);

        // 画面遷移先を指定
        mav.setViewName("top");

        mav.addObject("messages", messageList);
        mav.addObject("comments", commentList);

        String bindingResultKey = org.springframework.validation.BindingResult.MODEL_KEY_PREFIX + "commentForm";

        if (session.getAttribute(bindingResultKey) != null) {
            mav.addObject(bindingResultKey, session.getAttribute(bindingResultKey));
            mav.addObject("commentForm", session.getAttribute("commentForm"));

            // 一度画面に渡したらセッションから削除する
            session.removeAttribute(bindingResultKey);
            session.removeAttribute("commentForm");
        } else {
            // 通常アクセス時は、エラー情報の入っていない綺麗な空のフォームを置く
            mav.addObject("commentForm", new CommentForm());
        }

        if (session.getAttribute("errorMessage") != null) {
            mav.addObject("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage");
        }

        return mav;
    }

    @GetMapping("/search")
    public ModelAndView categorize(
            @RequestParam(name = "startDate", required = false) String startStr,
            @RequestParam(name = "endDate", required = false) String endStr,
            @RequestParam(name = "category", required = false) String category){

        log.info("[TopController] Received request to search. start: {}, end: {}, category: {}", startStr, endStr, category);

        ModelAndView mav = new ModelAndView();
        List<MessageForm> messageList;

        try {
            messageList = messageService.findByCategorize(startStr, endStr, category);
        } catch (DateTimeParseException e) {
            log.warn("[TopController] Invalid date format submitted: {}", e.getMessage());
            mav.addObject("errorMessage", "E0025");

            // エラー時は安全のために全件取得にする
            messageList = messageService.findAllMessages();
        }

        List<CommentForm> commentList = commentService.findAllComments(messageList);

        //画面（View）とデータのセット
        mav.setViewName("top");
        mav.addObject("startDate", startStr);
        mav.addObject("endDate", endStr);
        mav.addObject("category", category);
        mav.addObject("messages", messageList);
        mav.addObject("comments", commentList);
        mav.addObject("commentForm", new CommentForm());

        return mav;
    }
}