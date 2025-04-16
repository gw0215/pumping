package com.pumping.domain.member.fixture;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.model.Member;

public abstract class MemberFixture {

    public static final String NICKNAME = "its_woo";
    public static final String EMAIL = "email@fit.com";
    public static final String PASSWORD = "abc12345678@";
    public static final String PROFILE_IMAGE_PATH = "imagepath";

    private static byte[] IMAGE = new byte[]{1, 2, 3, 4, 5};

    public static Member createMember() {
        return new Member(NICKNAME, EMAIL, PASSWORD, PROFILE_IMAGE_PATH);
    }

    public static MemberSignUpRequest createMemberSignUpRequest() {
        return new MemberSignUpRequest(PASSWORD, EMAIL, NICKNAME);
    }

    public static VerifyPasswordRequest createDeleteMemberRequest() {
        return new VerifyPasswordRequest(PASSWORD);
    }

}
