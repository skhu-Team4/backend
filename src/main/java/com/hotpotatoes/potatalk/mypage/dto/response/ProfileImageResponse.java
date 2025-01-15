package com.hotpotatoes.potatalk.mypage.dto.response;

import com.hotpotatoes.potatalk.mypage.entity.ProfileImageType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Getter
@Builder
public class ProfileImageResponse {
    private List<String> availableImageIds;
    private String currentImageId;

    public static ProfileImageResponse of(String currentImageId) {
        List<String> imageIds = Arrays.stream(ProfileImageType.values())
                .map(ProfileImageType::getImageId)
                .collect(Collectors.toList());

        return ProfileImageResponse.builder()
                .availableImageIds(imageIds)
                .currentImageId(currentImageId)
                .build();
    }
}
