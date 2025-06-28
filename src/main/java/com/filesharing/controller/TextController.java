package com.filesharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.filesharing.model.TextEntity;
import com.filesharing.service.TextService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/texts")
@CrossOrigin(origins = "*")
public class TextController {

    @Autowired
    private TextService textService;

    @PostMapping("/upload")
    public ResponseEntity<TextUploadResponse> uploadText(
            @RequestParam("content") String content,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String password) {
        try {
            TextEntity textEntity = textService.storeText(content, remark, password);
            TextUploadResponse response = new TextUploadResponse(
                textEntity.getId(),
                textEntity.getContent(),
                textEntity.getRemark(),
                textEntity.getCreateTime()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<String> verifyPassword(@PathVariable Long id, @RequestParam String password) {
        try {
            TextEntity textEntity = textService.getTextWithPassword(id, password);
            return ResponseEntity.ok("Password verified successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TextResponse> getText(
            @PathVariable Long id,
            @RequestParam(required = false) String password) {
        try {
            TextEntity textEntity = password != null ?
                    textService.getTextWithPassword(id, password) :
                    textService.getText(id);

            String textUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/texts/")
                    .path(String.valueOf(textEntity.getId()))
                    .toUriString();

            TextResponse response = new TextResponse(
                textEntity.getId(),
                textEntity.getContent(),
                textEntity.getRemark(),
                textEntity.getCreateTime(),
                textEntity.getPassword() != null  // 添加是否需要密码的标志
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<TextResponse>> getAllTexts() {
        try {
            List<TextEntity> texts = textService.getAllTexts();
            List<TextResponse> responses = texts.stream()
                .map(text -> new TextResponse(
                    text.getId(),
                    text.getPassword() != null ? "******" : text.getContent(),
                    text.getRemark(),
                    text.getCreateTime(),
                    text.getPassword() != null  // 添加是否需要密码的标志
                ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private static class TextResponse {
        private Long id;
        private String content;
        private String remark;
        private Long createTime;
        private boolean passwordProtected;  // 新增字段

        public TextResponse(Long id, String content, String remark, Long createTime, boolean passwordProtected) {
            this.id = id;
            this.content = content;
            this.remark = remark;
            this.createTime = createTime;
            this.passwordProtected = passwordProtected;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public boolean isPasswordProtected() {
            return passwordProtected;
        }

        public void setPasswordProtected(boolean passwordProtected) {
            this.passwordProtected = passwordProtected;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteText(@PathVariable Long id) {
        try {
            textService.deleteText(id);
            return ResponseEntity.ok("Text deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not delete text: " + e.getMessage());
        }
    }

    private static class TextUploadResponse {
        private Long id;
        private String content;
        private String remark;
        private Long createTime;

        public TextUploadResponse(Long id, String content, String remark, Long createTime) {
            this.id = id;
            this.content = content;
            this.remark = remark;
            this.createTime = createTime;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }


        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }
    }

   
}