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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MessageService {

    private static final int PAGE_SIZE = 100;

    @Autowired
    MessageRepository messageRepository;

    public List<MessageForm> findAllMessages(String startStr, String endStr){

        LocalDateTime start = LocalDateTime.of(1970, 1, 1, 0, 0);
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
        
    }

}
