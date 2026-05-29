package com.example.ariga_seiya_forum.service;

import com.example.ariga_seiya_forum.controller.form.MessageForm;
import com.example.ariga_seiya_forum.entity.Message;
import com.example.ariga_seiya_forum.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MessageService {

    private static final int PAGE_SIZE = 100;

    @Autowired
    MessageRepository messageRepository;

    public List<MessageForm> findAllMessages(){

        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 0, 0,0);
        LocalDateTime end = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("createdDate").descending());

        List<Message> results = messageRepository.findAllByCreatedDateBetween(start, end, pageable);

        log.info("[MessageService] Found {} message for display.", results.size());

        return setMessageForm(results);
    }

    private List<MessageForm> setMessageForm(List<Message> results) {

        log.info("[MessageService] Converting Entity to Form - Count: {} items", results.size());
        List<MessageForm> messageList = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            MessageForm messageForm = new MessageForm();
            Message message = results.get(i);

            messageForm.setId(message.getId());
            messageForm.setTitle(message.getTitle());
            messageForm.setContent(message.getContent());
            messageForm.setCategory(message.getCategory());
            messageForm.setCreatedDate(message.getCreatedDate());
            messageForm.setUpdatedDate(message.getUpdatedDate());

            // 💡【追加】JPAで結合したUserから、ユーザー名とアカウントを抜いてFormに設定する
            if (message.getUser() != null) {
                messageForm.setUserId(message.getUser().getId());        //ユーザーID
                messageForm.setName(message.getUser().getName());       // ユーザー名
                messageForm.setAccount(message.getUser().getAccount()); // アカウント名
            }

            messageList.add(messageForm);
        }
        return messageList;
    }

    public List<MessageForm> findByCategorize(String startStr, String endStr, String category){


        log.info("[MessageService] Starting search - StartStr: '{}', EndStr: '{}', Category: '{}'",
               startStr, endStr, category);

        LocalDateTime start;
        if (startStr == null || startStr.isBlank()) {
            start = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        } else {
            // 文字列を一度LocalDateとして解析し、一日の始まり（00:00:00）を結合する
            start = LocalDate.parse(startStr).atStartOfDay();
        }

        LocalDateTime end;
        if (endStr == null || endStr.isBlank()) {
            end = LocalDateTime.now();
        } else {
            // 文字列を一度LocalDateとして解析し、一日の終わり（23:59:59）を結合する
            end = LocalDate.parse(endStr).atTime(23, 59, 59);
        }

        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("createdDate").descending());
        List<Message> results;

        if (category == null || category.isBlank()) {
            results = messageRepository.findAllByCreatedDateBetween(start, end, pageable);
        } else {
            results = messageRepository.findAllByCreatedDateBetweenAndCategory(start, end, category, pageable);
        }

        log.info("[MessageService] Found {} message for display.", results.size());

        return setMessageForm(results);
    }
}
