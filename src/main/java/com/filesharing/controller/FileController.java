package com.filesharing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.filesharing.config.FileUploadConfig;
import com.filesharing.model.FileEntity;
import com.filesharing.service.FileService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Autowired
    private FileService fileService;

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxFileSize", fileUploadConfig.getMaxFileSize());
        return config;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String password) {
        // 检查文件大小是否超过限制
        if (file.getSize() > fileUploadConfig.getMaxFileSize() * 1024 * 1024) {
            throw new RuntimeException("File size exceeds the limit of " + fileUploadConfig.getMaxFileSize() + "MB");
        }
        try {
            FileEntity fileEntity = fileService.storeFile(file, remark, password);
            FileUploadResponse response = new FileUploadResponse(
                    fileEntity.getId(),
                    fileEntity.getFileName(),
                    fileEntity.getFileType(),
                    file.getSize(),
                    fileEntity.getRemark(),
                    fileEntity.getCreateTime()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<String> verifyPassword(@PathVariable Long id, @RequestParam String password) {
        try {
            FileEntity fileEntity = fileService.getFileWithPassword(id, password);
            return ResponseEntity.ok("Password verified successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable Long id,
            @RequestParam(required = false) String password) {
        try {
            FileEntity fileEntity = password != null ?
                    fileService.getFileWithPassword(id, password) :
                    fileService.getFile(id);

            if (fileEntity.getData() == null || fileEntity.getData().length == 0) {
                return ResponseEntity.badRequest().body(null);
            }

            String fileName = fileEntity.getFileName();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "download";
            }

            // 设置响应头，强制浏览器下载文件而不是在浏览器中打开
            // 使用二进制类型的Content-Type并设置文件范围
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            String disposition = String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s",
                    fileName.replace("\\", "\\\\").replace("\"", "\\\""),
                    encodedFileName);
            byte[] fileData = fileEntity.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/x-binary"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                    .header("X-Content-Type-Options", "nosniff")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.length))
                    .header(HttpHeaders.CONTENT_RANGE, "bytes 0-" + (fileData.length - 1) + "/" + fileData.length)
                    .body(fileData);
        } catch (Exception e) {
            e.printStackTrace(); // 打印错误堆栈以便调试
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not delete file: " + e.getMessage());
        }
    }

    @PostMapping("/upload/text")
    public ResponseEntity<String> uploadText(@RequestParam("content") String content, @RequestParam("fileName") String fileName) {
        try {
            byte[] textBytes = content.getBytes();
            FileEntity fileEntity = fileService.storeText(fileName, textBytes);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(String.valueOf(fileEntity.getId()))
                    .toUriString();

            return ResponseEntity.ok(fileDownloadUri);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not upload text: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FileInfo>> getAllFiles() {
        List<FileInfo> files = fileService.getAllFiles().stream().map(file -> {
            return new FileInfo(
                    file.getId(),
                    file.getFileName(),
                    file.getFileType(),
                    file.getData().length,
                    file.getCreateTime(),
                    file.getRemark(),
                    file.getPassword() != null  // 添加是否需要密码的标志
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(files);
    }

    private static class FileUploadResponse {
        private Long id;
        private String fileName;
        private String fileType;
        private long size;
        private String remark;
        private Long createTime;

        public FileUploadResponse(Long id, String fileName, String fileType, long size, String remark, Long createTime) {
            this.id = id;
            this.fileName = fileName;
            this.fileType = fileType;
            this.size = size;
            this.remark = remark;
            this.createTime = createTime;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public long getSize() {
            return size;
        }

        public String getRemark() {
            return remark;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public Long getId() {
            return id;
        }
    }

    private static class FileInfo {
        private Long id;
        private String fileName;
        private String fileType;
        private long size;
        private Long createTime;
        private String remark;
        private boolean passwordProtected;  // 新增字段

        public FileInfo(Long id, String fileName, String fileType, long size, Long createTime, String remark, boolean passwordProtected) {
            this.id = id;
            this.fileName = fileName;
            this.fileType = fileType;
            this.size = size;
            this.createTime = createTime;
            this.remark = remark;
            this.passwordProtected = passwordProtected;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public long getSize() {
            return size;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public String getRemark() {
            return remark;
        }

        public boolean isPasswordProtected() {
            return passwordProtected;
        }

        public Long getId() {
            return id;
        }
    }
}


