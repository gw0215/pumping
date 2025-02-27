package com.pumping.domain.member.repository;

import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class MemberJdbcRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void save() {

        Member member = MemberFixture.createMember();
        Member saveMember = memberRepository.save(member);

        Optional<Member> optionalMember = memberRepository.findById(saveMember.getId());

        Assertions.assertThat(optionalMember).isPresent();
        Member findMember = optionalMember.get();

        Assertions.assertThat(findMember).isEqualTo(saveMember);

    }
}