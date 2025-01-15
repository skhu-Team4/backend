package com.hotpotatoes.potatalk.mypage.service;

import com.hotpotatoes.potatalk.mypage.dto.request.ProfileImageRequest;
import com.hotpotatoes.potatalk.mypage.dto.response.ProfileImageResponse;
import com.hotpotatoes.potatalk.mypage.entity.ProfileImageType;
import com.hotpotatoes.potatalk.user.entity.User;
import com.hotpotatoes.potatalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    
    // 서비스의 전체 프로필 사진 이미지, 현재 자신의 프로필사진url도 함께 응답
    @Transactional(readOnly = true)
    public ProfileImageResponse getProfileImages(Principal principal) {
        User user = getUserFromPrincipal(principal);
        String currentImageId = user.getProfileImageUrl(); // 나의 프로필사진url
        return ProfileImageResponse.of(currentImageId);
    }
    
    // 프로필 사진 변경
    @Transactional
    public void updateProfileImage(ProfileImageRequest request, Principal principal) {
        User user = getUserFromPrincipal(principal);

        // ProfileImageType에 존재하는 이미지id인지 확인
        ProfileImageType.fromImageId(request.getImageId());

        // imageId 저장
        user.setProfileImageUrl(request.getImageId());
        userRepository.save(user);
    }

    private User getUserFromPrincipal(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
