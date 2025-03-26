package com.pumping.domain.member.service;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
            return new byte[0];
        }
    }

    @Transactional
    public void delete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 회원 ID : " + memberId));
        member.deleteMember();
    }

    @Transactional(readOnly = true)
    public byte[] getProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 회원 ID : " + memberId));
        return member.getProfileImage();
    }

    @Transactional
    public void updateProfileImage(Long memberId, MultipartFile file) {
        Member member1 = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 회원 ID : " + memberId));

        byte[] data;
        try {
            data = file.getBytes();
            member1.updateMemberProfileImage(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyPassword(Member member, String password) {
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

}
