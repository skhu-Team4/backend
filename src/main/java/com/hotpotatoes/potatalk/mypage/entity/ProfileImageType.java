package com.hotpotatoes.potatalk.mypage.entity;

public enum ProfileImageType {
    // 서비스의 기본 이미지가 3개라고 가정하고 필드 생성, 이 부분은 논의 필요
    PROFILE_1("profile1"),
    PROFILE_2("profile2"),
    PROFILE_3("profile3");
    
    private final String imageId;

    ProfileImageType(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public static ProfileImageType fromImageId(String imageId) {
        for (ProfileImageType type : ProfileImageType.values()) {
            if (type.getImageId().equals(imageId)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid image id: " + imageId);
    }
}
