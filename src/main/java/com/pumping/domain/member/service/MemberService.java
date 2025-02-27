package com.pumping.domain.member.service;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void save(MemberSignUpRequest memberSignUpRequest) {

        String encodePassword = bCryptPasswordEncoder.encode(memberSignUpRequest.getPassword());
        Member member = new Member(memberSignUpRequest.getNickname(), memberSignUpRequest.getEmail(), encodePassword, memberSignUpRequest.getProfileImage());
        memberRepository.save(member);
    }

}
