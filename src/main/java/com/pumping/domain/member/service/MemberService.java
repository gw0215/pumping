package com.pumping.domain.member.service;

import com.pumping.domain.emailverification.model.EmailVerification;
import com.pumping.domain.emailverification.repository.EmailVerificationRepository;
import com.pumping.domain.member.dto.EmailCodeCheckRequest;
import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.global.common.util.MailService;
import com.pumping.global.common.util.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final MailService mailService;

    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    public Long save(MemberSignUpRequest memberSignUpRequest) {

        String encodePassword = bCryptPasswordEncoder.encode(memberSignUpRequest.getPassword());
        Member member = new Member(memberSignUpRequest.getNickname(), memberSignUpRequest.getEmail(), encodePassword, memberSignUpRequest.getProfileImage());
        Member saveMember = memberRepository.save(member);
        return saveMember.getId();
    }

    @Transactional
    public void delete(String password, Member member) {

        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        memberRepository.delete(member);

    }

    public void sendCodeEmail(String email) {
        String code = RandomCodeGenerator.generateCode();
        mailService.sendCodeEmail(code, email);
    }

    @Transactional(readOnly = true)
    public void checkCode(EmailCodeCheckRequest emailCodeCheckRequest) {

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(emailCodeCheckRequest.getEmail())
                .orElseThrow(RuntimeException::new);

        String code = emailVerification.getCode();

        if (!Objects.equals(code, emailCodeCheckRequest.getCode())) {
            throw new RuntimeException();
        }

    }

}
