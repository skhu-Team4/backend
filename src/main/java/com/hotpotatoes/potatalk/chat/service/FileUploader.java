package com.hotpotatoes.potatalk.chat.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploader {
    private static final String PHOTO_UPLOAD_DIR = "uploads/photos/";
    private static final String VIDEO_UPLOAD_DIR = "uploads/videos/";

    // 사진 저장 메서드
    public static String savePhoto(MultipartFile file) throws IOException {
        File dir = new File(PHOTO_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리가 없으면 생성
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destination = new File(dir, fileName);
        file.transferTo(destination);

        return "/uploads/photos/" + fileName;
    }

    // 영상 저장 메서드
    public static String saveVideo(MultipartFile file) throws IOException {
        File dir = new File(VIDEO_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리가 없으면 생성
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destination = new File(dir, fileName);
        file.transferTo(destination);

        return "/uploads/videos/" + fileName;
    }
}
