package com.pumping.domain.routine.repository;

import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import com.pumping.domain.routine.fixture.RoutineFixture;
import com.pumping.domain.routine.model.Routine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoutineRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoutineRepository routineRepository;

    @Test
    void 회원의_루틴_전체_조회_테스트() {

        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        List<Routine> routines = RoutineFixture.createRoutines(member, "루틴이름",5);
        routineRepository.saveAll(routines);

        List<Routine> routineList = routineRepository.findAllByMemberId(member.getId());

        Assertions.assertThat(routineList).hasSize(routines.size());
        routineList.forEach(routine -> Assertions.assertThat(routine.getMember().getId()).isEqualTo(member.getId()));

    }

}