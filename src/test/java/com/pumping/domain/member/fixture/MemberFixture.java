package com.pumping.domain.member.fixture;

import com.pumping.domain.member.dto.LoginRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.dto.VerifyPasswordRequest;
import com.pumping.domain.member.model.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MemberFixture {

    public static final String NICKNAME = "its_woo1111";
    public static final String EMAIL = "email@pumping.com1111";
    public static final String PASSWORD = "abc12345678@1111";
    public static final String PROFILE_IMAGE_PATH = "imagepath1111";

    private static byte[] IMAGE = new byte[]{1, 2, 3, 4, 5};

    public static Member createMember() {
        return new Member(NICKNAME, EMAIL, PASSWORD, PROFILE_IMAGE_PATH);
    }
    public static Member createMemberWithId(Long id) {
        Member member = new Member(NICKNAME, EMAIL, PASSWORD, PROFILE_IMAGE_PATH);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static Member createMember(String nickname, String email, String password) {
        return new Member(nickname, email, password, PROFILE_IMAGE_PATH);
    }

    public static List<Member> createMembers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createMember(
                        NICKNAME+ i,
                        EMAIL+i,
                        PASSWORD + i
                ))
                .collect(Collectors.toList());
    }

    public static MemberSignUpRequest createMemberSignUpRequest() {
        return new MemberSignUpRequest(PASSWORD, EMAIL, NICKNAME);
    }

    public static VerifyPasswordRequest createDeleteMemberRequest() {
        return new VerifyPasswordRequest(PASSWORD);
    }

    public static LoginRequest createLoginRequest() {
        return new LoginRequest(EMAIL, PASSWORD);
    }

    public static VerifyPasswordRequest createVerifyPasswordRequest() {
        return new VerifyPasswordRequest(PASSWORD);
    }
}
