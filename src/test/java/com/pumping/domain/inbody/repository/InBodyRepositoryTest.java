package com.pumping.domain.inbody.repository;

import com.pumping.domain.inbody.fixture.InBodyFixture;
import com.pumping.domain.inbody.model.InBody;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InBodyRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    InBodyRepository inBodyRepository;

    @Test
    void 사용자의_최신_인바디_기록_조회_테스트() {

        Member member = memberRepository.save(MemberFixture.createMember());

        InBody inbody1 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(1L));
        inBodyRepository.save(inbody1);
        InBody inbody2 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(2L));
        inBodyRepository.save(inbody2);
        InBody inbody3 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(3L));
        inBodyRepository.save(inbody3);
        InBody inbody4 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(4L));
        inBodyRepository.save(inbody4);

        Optional<InBody> optionalInBody = inBodyRepository.findTopByMemberOrderByDateDesc(member);

        Assertions.assertThat(optionalInBody).isPresent();
        Assertions.assertThat(optionalInBody.get()).isEqualTo(inbody1);

    }

    @Test
    void 사용자의_인바디_기록_날짜_기간_조회_테스트() {

        Member member = memberRepository.save(MemberFixture.createMember());

        InBody inbody1 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(1L));
        inBodyRepository.save(inbody1);
        InBody inbody2 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(2L));
        inBodyRepository.save(inbody2);
        InBody inbody3 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(3L));
        inBodyRepository.save(inbody3);
        InBody inbody4 = InBodyFixture.createInbody(member, LocalDate.now().minusDays(4L));
        inBodyRepository.save(inbody4);
        InBody inbody5 = InBodyFixture.createInbody(member, LocalDate.now().plusDays(1L));
        inBodyRepository.save(inbody5);
        InBody inbody6 = InBodyFixture.createInbody(member, LocalDate.now().plusDays(2L));
        inBodyRepository.save(inbody6);
        InBody inbody7 = InBodyFixture.createInbody(member, LocalDate.now().plusDays(3L));
        inBodyRepository.save(inbody7);
        InBody inbody8 = InBodyFixture.createInbody(member, LocalDate.now().plusDays(4L));
        inBodyRepository.save(inbody8);

        List<InBody> inBodies = inBodyRepository.findByMemberAndDateBetween(member, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));

        Assertions.assertThat(inBodies).hasSize(8);

    }

    @Test
    void 사용자의__인바디_기록_날짜_조회_테스트() {

        Member member = memberRepository.save(MemberFixture.createMember());

        InBody inbody = InBodyFixture.createInbody(member, LocalDate.now().minusDays(1L));
        inBodyRepository.save(inbody);

        Optional<InBody> optionalInBody = inBodyRepository.findByMemberAndDate(member, inbody.getDate());

        Assertions.assertThat(optionalInBody).isPresent();
        Assertions.assertThat(optionalInBody.get()).isEqualTo(inbody);

    }

}