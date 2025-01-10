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

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMediaService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatPhotoRepository chatPhotoRepository;
    private final ChatVideoRepository chatVideoRepository;

    @Transactional
    public String savePhoto(int chatId, MultipartFile file) throws IOException {
        // 채팅방 조회
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다. id: " + chatId);
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // FileUploader 클래스의 savePhoto 메서드를 사용하여 파일을 저장
        String photoUrl = FileUploader.savePhoto(file);

        // 사진 URL을 데이터베이스에 저장
        ChatPhoto chatPhoto = new ChatPhoto();
        chatPhoto.setPhoto_url(photoUrl);
        chatPhoto.setChatRoom(chatRoom);

        chatPhotoRepository.save(chatPhoto);

        return chatPhoto.getPhoto_url(); // 저장된 사진 URL 반환
    }

    @Transactional
    public String saveVideo(int chatId, MultipartFile file) throws IOException {
        // 채팅방 조회
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다. id: " + chatId);
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // FileUploader 클래스의 saveVideo 메서드를 사용하여 영상 파일을 저장
        String videoUrl = FileUploader.saveVideo(file);

        // 영상 URL을 데이터베이스에 저장
        ChatVideo chatVideo = new ChatVideo();
        chatVideo.setVideo_url(videoUrl);
        chatVideo.setChatRoom(chatRoom);

        chatVideoRepository.save(chatVideo);

        return chatVideo.getVideo_url(); // 저장된 영상 URL 반환
    }
}
