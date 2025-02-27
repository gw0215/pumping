package com.pumping.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignUpRequest {

    private String password;

    private String email;

    private String nickname;

    private String profileImage;

    public MemberSignUpRequest(String password, String email, String nickname, String profileImage) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
