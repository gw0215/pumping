package com.pumping.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteMemberRequest {

    private String password;

    public DeleteMemberRequest(String password) {
        this.password = password;
    }

}
