package com.filesharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.filesharing.model.FileEntity;
import com.filesharing.repository.FileRepository;
import com.filesharing.util.PasswordUtil;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public FileEntity storeFile(MultipartFile file, String remark, String password) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String contentType = file.getContentType();
        
        // 如果ContentType为空，根据文件扩展名判断
        if (contentType == null || contentType.isEmpty()) {
            // 检查文件是否有后缀名
            if (fileName.contains(".")) {
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                switch (extension) {
                    case "pdf":
                        contentType = "application/pdf";
                        break;
                    case "doc":
                        contentType = "application/msword";
                        break;
                    case "docx":
                        contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        break;
                    case "xls":
                        contentType = "application/vnd.ms-excel";
                        break;
                    case "xlsx":
                        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                        break;
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                    default:
                        contentType = "application/octet-stream";
                }
            } else {
                // 如果文件没有后缀名，设置为二进制流类型
                contentType = "application/octet-stream";
                // 将文件名作为下载时的默认名称
                fileName = fileName;
            }
        }

        String encryptedPassword = password != null ? PasswordUtil.encryptPassword(password) : null;
        FileEntity fileEntity = new FileEntity(fileName, contentType, file.getBytes(), remark, encryptedPassword);
        return fileRepository.save(fileEntity);
    }

    public FileEntity getFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
        if (file.getPassword() != null) {
            file.setFileName("*****");
        }
        return file;
    }

    public FileEntity getFileWithPassword(Long fileId, String password) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
        if (file.getPassword() != null) {
            if (!PasswordUtil.verifyPassword(password, file.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
        }
        return file;
    }

    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll(org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Direction.DESC, "createTime"));
    }

    public void deleteFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
        fileRepository.delete(file);
    }

    public FileEntity storeText(String fileName, byte[] content) {
        // 对于文本内容，使用application/octet-stream类型，避免浏览器自动添加.txt后缀
        FileEntity fileEntity = new FileEntity(fileName, "application/octet-stream", content, null, null);
        return fileRepository.save(fileEntity);
    }
}