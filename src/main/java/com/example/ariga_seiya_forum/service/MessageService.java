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
            results = messageRepository.findAllByCreatedDateBetweenAndCategoryContaining(start, end, category, pageable);
        }

        log.info("[MessageService] Found {} message for display.", results.size());

        return setMessageForm(results);
    }

    /*
     * 保存処理（新規登録・更新）
     */
    public void saveMessage(MessageForm reqMessage) {

        log.info("[MessageService] Creating new message - Content: {}", reqMessage.getContent());

        // Entityへの変換
        Message saveMessage = setMessageEntity(reqMessage);

        // DBへの保存実行
        messageRepository.save(saveMessage);

        // 2. 正常終了を記録
        log.info("[ReportService] Message save operation completed successfully.");
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Message setMessageEntity(MessageForm reqMessage){
        log.info("[MessageService] Converting Form to Entity - ID: {}", reqMessage.getId());

        Message message = new Message();
        message.setUserId(reqMessage.getUserId());
        message.setContent(reqMessage.getContent());
        message.setCategory(reqMessage.getCategory());
        message.setTitle(reqMessage.getTitle());
        return message;
    }

    public void deleteMessage(int id) {
        log.warn("[MessageService] Executing delete - MessageID: {}", id);

        messageRepository.deleteById(id);

        log.info("[MessageService] Delete completed successfully - MessageID: {}", id);

    }

    /*
     * 投稿を1件取得
     */
    public MessageForm findMessageById(Integer id) {

        log.info("[MessageService] Attempting to find message by ID: {}", id);

        List<Message> results = new ArrayList<>();
        Message message = messageRepository.findById(id).orElse(null);

        if (message == null) {
            log.warn("[MessageService] Message not found - MessageID: {}", id);
            return null;
        }

        log.info("[MessageService] Message successfully retrieved from database. Title: {}", message.getTitle());

        results.add(message);
        List<MessageForm> messagList = setMessageForm(results);

        return messagList.get(0);
    }
}
