package com.pumping.domain.member.fixture;

import com.pumping.domain.member.dto.DeleteMemberRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.model.Member;

public abstract class MemberFixture {

    public static final String NICKNAME = "its_woo";
    public static final String EMAIL = "email@fit.com";
    public static final String PASSWORD = "abc12345678@";
    public static final String PROFILE_IMAGE = "profileImage";

    public static Member createMember() {
        return new Member(NICKNAME, EMAIL, PASSWORD, PROFILE_IMAGE);
    }

    public static MemberSignUpRequest createMemberSignUpRequest() {
        return new MemberSignUpRequest(PASSWORD, EMAIL, NICKNAME, PROFILE_IMAGE);
    }

    public static DeleteMemberRequest createDeleteMemberRequest() {
        return new DeleteMemberRequest(PASSWORD);
    }

}
