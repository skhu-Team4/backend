package com.hotpotatoes.potatalk.user.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class UserSignUpDto {
    private String name;
    private String loginId;
    private String email;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$",
            message = "비밀번호는 최소 하나의 알파벳과 숫자를 포함해야 하며, 특수문자는 사용할 수 없습니다."
    )
    @Pattern(
            regexp = "^[^ㄱ-ㅎ가-힣]*$",
            message = "비밀번호에 한글을 포함할 수 없습니다."
    )
    private String password;
    private String phoneNumber;
    private String profileImageUrl;
    private String introduction;
}
