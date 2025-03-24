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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
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
        Member member = new Member(memberSignUpRequest.getNickname(), memberSignUpRequest.getEmail(), encodePassword, loadDefaultProfileImage());
        Member saveMember = memberRepository.save(member);
        return saveMember.getId();
    }

    private byte[] loadDefaultProfileImage() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/default_profile.jpg");
            return (inputStream != null) ? inputStream.readAllBytes() : new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Transactional
    public void delete(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(RuntimeException::new);

        member.deleteMember();

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

    @Transactional(readOnly = true)
    public byte[] getProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(RuntimeException::new);

        return member.getProfileImage();
    }

    @Transactional
    public void updateProfileImage(Member member, MultipartFile file) {

        Member member1 = memberRepository.findById(member.getId()).orElseThrow(RuntimeException::new);

        byte[] data = null;
        try {
            data = file.getBytes();
            member1.updateMemberProfileImage(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyPassword(Member member, String password) {
        return bCryptPasswordEncoder.matches(password, member.getPassword());
    }

}
