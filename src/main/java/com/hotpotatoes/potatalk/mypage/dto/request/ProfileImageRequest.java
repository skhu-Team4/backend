package com.hotpotatoes.potatalk.mypage.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileImageRequest {
    private String imageId;  // 프론트에서 관리할 이미지id
}
