package com.pumping.global.common;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestMemberInitializer {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void createTestUser() {

        if (!memberRepository.existsByEmailAndDeletedFalse("test_user@pumping.run")) {
            MemberSignUpRequest tester = new MemberSignUpRequest("Test1234!", "test_user@pumping.run", "ROLE_TESTER");
            memberService.save(tester);
        }

    }

}
