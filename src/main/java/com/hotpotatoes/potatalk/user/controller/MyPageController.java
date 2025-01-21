package com.hotpotatoes.potatalk.user.controller;

import com.hotpotatoes.potatalk.user.dto.ProfileImageRequest;
import com.hotpotatoes.potatalk.user.dto.ProfileImageResponse;
import com.hotpotatoes.potatalk.user.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    
    // 서비스 내 사용 가능한 프로필 이미지 조회
    @GetMapping("/profile-images")
    public ResponseEntity<ProfileImageResponse> getProfileImages(Principal principal) {
        ProfileImageResponse response = myPageService.getProfileImages(principal);
        return ResponseEntity.ok(response);
    }
    
    // 프로필 이미지 변경
    @PutMapping("/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @RequestBody ProfileImageRequest request,
            Principal principal) {
        myPageService.updateProfileImage(request, principal);
        return ResponseEntity.ok().build();
    }
}
