package com.pumping.domain.performedroutine.repository;

import com.pumping.domain.performedroutine.model.PerformedRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PerformedRoutineRepository extends JpaRepository<PerformedRoutine, Long> {

    @Query("SELECT pr FROM PerformedRoutine pr " +
            "JOIN Routine r ON r.id = pr.routine.id " +
            "WHERE r.member.id = :memberId AND pr.performedDate = :performedDate")
    Optional<PerformedRoutine> findByMemberIdAndPerformedDate(@Param("memberId") Long memberId, @Param("performedDate") LocalDate performedDate);

}
