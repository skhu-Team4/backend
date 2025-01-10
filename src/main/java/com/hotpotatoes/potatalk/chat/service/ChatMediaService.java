package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.domain.ChatPhoto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.domain.ChatVideo;
import com.hotpotatoes.potatalk.chat.repository.ChatPhotoRepository;
import com.hotpotatoes.potatalk.chat.repository.ChatRoomRepository;
import com.hotpotatoes.potatalk.chat.repository.ChatVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMediaService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPhotoRepository chatPhotoRepository;
    private final ChatVideoRepository chatVideoRepository;

    @Transactional
    public String savePhoto(int chatId, MultipartFile file) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다. id: " + chatId);
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // 파일 저장
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File savedFile = new File("uploads/photos/" + fileName);
        try {
            file.transferTo(savedFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }

        // 데이터베이스에 저장
        ChatPhoto chatPhoto = new ChatPhoto();
        chatPhoto.setPhoto_url("/uploads/photos/" + fileName);
        chatPhoto.setChatRoom(chatRoom);

        chatPhotoRepository.save(chatPhoto);

        return chatPhoto.getPhoto_url();
    }

    @Transactional
    public String saveVideo(int chatId, MultipartFile file) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다. id: " + chatId);
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // 파일 저장
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File savedFile = new File("uploads/videos/" + fileName);
        try {
            file.transferTo(savedFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }

        // 데이터베이스에 저장
        ChatVideo chatVideo = new ChatVideo();
        chatVideo.setVideo_url("/uploads/videos/" + fileName);
        chatVideo.setChatRoom(chatRoom);

        chatVideoRepository.save(chatVideo);

        return chatVideo.getVideo_url();
    }
}
