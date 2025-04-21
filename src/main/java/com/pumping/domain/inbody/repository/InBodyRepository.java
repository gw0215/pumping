package com.pumping.domain.inbody.repository;

import com.pumping.domain.inbody.model.InBody;
import com.pumping.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InBodyRepository extends JpaRepository<InBody,Long> {

    Optional<InBody> findTopByMemberOrderByDateDesc(Member member);

    List<InBody> findByMemberAndDateBetween(Member member, LocalDate from, LocalDate to);

    Optional<InBody> findByMemberAndDate(Member member, LocalDate date);

}
