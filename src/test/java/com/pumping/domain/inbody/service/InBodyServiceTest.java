package com.pumping.domain.inbody.service;

import com.pumping.domain.inbody.dto.InBodyResponse;
import com.pumping.domain.inbody.fixture.InBodyFixture;
import com.pumping.domain.inbody.model.InBody;
import com.pumping.domain.inbody.repository.InBodyRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InBodyServiceTest {

    @InjectMocks
    private InBodyService inBodyService;

    @Mock
    private InBodyRepository inBodyRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
    }

    @Test
    void save_기록_없을_경우_신규_저장() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(inBodyRepository.findByMemberAndDate(member, date)).thenReturn(Optional.empty());

        inBodyService.save(member, 70f, 30f, 15f, date);

        verify(inBodyRepository).save(any(InBody.class));
    }

    @Test
    void save_기존_기록_있는_경우_갱신_수행() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        InBody existing = mock(InBody.class);
        when(inBodyRepository.findByMemberAndDate(member, date)).thenReturn(Optional.of(existing));

        inBodyService.save(member, 71f, 31f, 16f, date);

        verify(existing).updateWeight(71f);
        verify(existing).updateSmm(31f);
        verify(existing).updateBfm(16f);
        verify(inBodyRepository, never()).save(any());
    }

    @Test
    void findRecentInBody_최근_기록_조회_성공() {
        InBody inbody = InBodyFixture.createInbody(member);
        when(inBodyRepository.findTopByMemberOrderByDateDesc(member)).thenReturn(Optional.of(inbody));

        InBodyResponse result = inBodyService.findRecentInBody(member);

        assertThat(result).isNotNull();
        assertThat(result.weight()).isEqualTo(inbody.getWeight());
        assertThat(result.date()).isEqualTo(inbody.getDate());
    }

    @Test
    void findRecentInBody_최근_기록_없을_경우_null_반환() {
        when(inBodyRepository.findTopByMemberOrderByDateDesc(member)).thenReturn(Optional.empty());

        InBodyResponse result = inBodyService.findRecentInBody(member);

        assertThat(result).isNull();
    }

    @Test
    void findByDate_날짜_범위_조회_성공() {
        LocalDate baseDate = LocalDate.of(2024, 1, 2);
        List<InBody> inBodies = InBodyFixture.createInBodies(member, 3, baseDate);
        when(inBodyRepository.findByMemberAndDateBetween(eq(member), any(), any())).thenReturn(inBodies);

        List<InBodyResponse> results = inBodyService.findByDate(member, baseDate.minusDays(1), baseDate.plusDays(1));

        assertThat(results).hasSize(3);
        assertThat(results.get(0).weight()).isEqualTo(inBodies.get(0).getWeight());
        assertThat(results).extracting(InBodyResponse::date).isSorted();
    }
}