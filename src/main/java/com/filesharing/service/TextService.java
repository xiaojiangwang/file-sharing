package com.filesharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.filesharing.model.TextEntity;
import com.filesharing.repository.TextRepository;
import com.filesharing.util.PasswordUtil;
import java.util.List;

@Service
public class TextService {

    @Autowired
    private TextRepository textRepository;
    
    public TextEntity storeText(String content, String remark, String password) {
        String encryptedPassword = password != null ? PasswordUtil.encryptPassword(password) : null;
        TextEntity textEntity = new TextEntity(content, remark, encryptedPassword);
        return textRepository.save(textEntity);
    }

    public TextEntity getText(Long textId) {
        TextEntity text = textRepository.findById(textId)
                .orElseThrow(() -> new RuntimeException("Text not found with id " + textId));
        if (text.getPassword() != null) {
            text.setContent("*****");
        }
        return text;
    }

    public TextEntity getTextWithPassword(Long textId, String password) {
        TextEntity text = textRepository.findById(textId)
                .orElseThrow(() -> new RuntimeException("Text not found with id " + textId));
        if (text.getPassword() != null) {
            if (!PasswordUtil.verifyPassword(password, text.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
        }
        return text;
    }

    public void deleteText(Long textId) {
        TextEntity text = textRepository.findById(textId)
                .orElseThrow(() -> new RuntimeException("Text not found with id " + textId));
        textRepository.delete(text);
    }

    public List<TextEntity> getAllTexts() {
        return textRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }
}