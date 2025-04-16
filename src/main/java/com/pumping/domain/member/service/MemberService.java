package com.pumping.domain.member.service;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {


    @Value("${file.upload-dir}")
    private String uploadDir;

    private final MemberRepository memberRepository;

    private static final Integer ITERATION = 65536;

    private static final Integer KEY_LENGTH = 256;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    @Transactional
    public Long save(MemberSignUpRequest memberSignUpRequest) {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        try {
            PBEKeySpec spec = new PBEKeySpec(memberSignUpRequest.getPassword().toCharArray(), salt, ITERATION, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodePassword = encodedSalt + "." + Base64.getEncoder().encodeToString(hash);
            Member member = new Member(memberSignUpRequest.getNickname(), memberSignUpRequest.getEmail(), encodePassword, "default_profile.jpg");
            Member saveMember = memberRepository.save(member);
            return saveMember.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }


    }

    public boolean verifyPassword(Member member, String password) {
        try {
            String[] parts = member.getPassword().split("\\.");

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] computedHash = skf.generateSecret(spec).getEncoded();

            if (!MessageDigest.isEqual(storedHash, computedHash)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 검증 중 오류 발생", e);
        }
    }

    @Transactional
    public void delete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 회원 ID : " + memberId));
        member.deleteMember();
    }

    @Transactional
    public void updateProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path savePath = Paths.get(uploadDir, fileName);

        try {
            file.transferTo(savePath.toFile());
            member.updateMemberProfileImage(fileName);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패", e);
        }
    }

    @Transactional(readOnly = true)
    public Member login(String email, String password) {
        Member member = memberRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 회원 이메일 : " + email));

        if (!verifyPassword(member, password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }


}
