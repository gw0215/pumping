package com.pumping.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberResponse {

    private String nickname;

    private String email;

    public MemberResponse(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
