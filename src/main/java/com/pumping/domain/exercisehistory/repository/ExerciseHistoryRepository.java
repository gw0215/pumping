package com.pumping.domain.exercisehistory.repository;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseHistoryRepository extends JpaRepository<ExerciseHistory, Long> {

    @Query("SELECT eh FROM ExerciseHistory eh " +
            "WHERE eh.member.id = :memberId AND eh.performedDate = :performedDate")
    Optional<ExerciseHistory> findByMemberIdAndPerformedDate(@Param("memberId") Long memberId, @Param("performedDate") LocalDate performedDate);

    List<ExerciseHistory> findByMemberAndPerformedDateBetween(Member member, LocalDate startDate, LocalDate endDate);

}
