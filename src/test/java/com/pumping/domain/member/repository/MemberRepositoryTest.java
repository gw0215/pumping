package com.pumping.domain.member.repository;

import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;


@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 삭제되지_않은_사용자_이메일로_확인() {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        boolean b = memberRepository.existsByEmailAndDeletedFalse(member.getEmail());

        Assertions.assertThat(b).isTrue();
    }

    @Test
    void 삭제되지_않은_사용자_이메일로_조회() {

        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        Optional<Member> optionalMember = memberRepository.findByEmailAndDeletedFalse(member.getEmail());

        Assertions.assertThat(optionalMember).isPresent();
        Assertions.assertThat(optionalMember.get()).isEqualTo(member);
    }


}
