package com.pumping.domain.member.service;

import com.pumping.domain.member.dto.MemberSignUpRequest;
import com.pumping.domain.member.exception.EmailAlreadyExistsException;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        Field uploadDirField = MemberService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(memberService, tempDir.toString());
    }

    @Test
    void 회원가입_성공하면_비밀번호가_암호화되어_저장된다() {
        MemberSignUpRequest request = MemberFixture.createMemberSignUpRequest();
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        memberService.save(request);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());

        Member saved = captor.getValue();
        assertThat(saved.getNickname()).isEqualTo(request.getNickname());
        assertThat(saved.getEmail()).isEqualTo(request.getEmail());
        assertThat(saved.getPassword()).contains(".");
    }

    @Test
    void 비밀번호가_일치하면_검증에_성공한다() {
        MemberSignUpRequest request = MemberFixture.createMemberSignUpRequest();
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        memberService.save(request);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        Member member = captor.getValue();

        assertThat(memberService.verifyPassword(member, request.getPassword())).isTrue();
    }

    @Test
    void 비밀번호가_다르면_검증에_실패한다() {
        MemberSignUpRequest request = MemberFixture.createMemberSignUpRequest();
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        memberService.save(request);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        Member member = captor.getValue();

        assertThat(memberService.verifyPassword(member, "틀린비밀번호")).isFalse();
    }

    @Test
    void 회원삭제_요청시_소프트삭제가_수행된다() {
        Member member = MemberFixture.createMember();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.delete(1L);

        assertThat(member.isDeleted()).isTrue();
    }

    @Test
    void 중복된_이메일이_존재하면_예외가_발생한다() {
        when(memberRepository.existsByEmailAndDeletedFalse("dup@email.com")).thenReturn(true);

        assertThatThrownBy(() -> memberService.checkDuplicationEmail("dup@email.com"))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void 로그인정보가_정상이면_회원이_반환된다() {
        MemberSignUpRequest request = MemberFixture.createMemberSignUpRequest();
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        memberService.save(request);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        Member member = captor.getValue();
        when(memberRepository.findByEmailAndDeletedFalse(request.getEmail())).thenReturn(Optional.of(member));

        Member result = memberService.login(request.getEmail(), request.getPassword());

        assertThat(result.getNickname()).isEqualTo(request.getNickname());
    }

    @Test
    void 로그인시_비밀번호가_틀리면_예외가_발생한다() {
        MemberSignUpRequest request = MemberFixture.createMemberSignUpRequest();
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        memberService.save(request);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        Member member = captor.getValue();
        when(memberRepository.findByEmailAndDeletedFalse(request.getEmail())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.login(request.getEmail(), "틀린비밀번호"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }

    @Test
    void 로그인시_이메일이_없으면_예외가_발생한다() {
        when(memberRepository.findByEmailAndDeletedFalse("no@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login("no@email.com", "password"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }

    @Test
    void 프로필이미지_업데이트_요청시_이미지가_저장되고_회원정보가_수정된다() throws IOException {
        Member member = MemberFixture.createMember();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());

        memberService.updateProfileImage(1L, file);

        assertThat(member.getProfileImagePath()).contains("test.png");
    }

    @Test
    void fcm토큰_저장요청시_회원의_토큰이_업데이트된다() {
        Member member = MemberFixture.createMember();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.saveFcmToken(1L, "fcm-token");

        assertThat(member.getFcmToken()).isEqualTo("fcm-token");
    }
}