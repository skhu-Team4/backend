package com.hotpotatoes.potatalk.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileImageRequest {
    private String imageId;  // 프론트에서 관리할 이미지id
}
