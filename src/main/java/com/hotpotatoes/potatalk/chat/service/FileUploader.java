package com.hotpotatoes.potatalk.chat.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploader {
    private static final String PHOTO_UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/photos/";
    private static final String VIDEO_UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/videos/";

    public static String savePhoto(MultipartFile file) throws IOException {
        File dir = new File(PHOTO_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destination = new File(dir, fileName);
        file.transferTo(destination);

        return "/uploads/photos/" + fileName;
    }

    public static String saveVideo(MultipartFile file) throws IOException {
        File dir = new File(VIDEO_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destination = new File(dir, fileName);
        file.transferTo(destination);

        return "/uploads/videos/" + fileName;
    }
}
