package com.pumping.domain.routine.repository;

import com.pumping.domain.routine.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT r FROM Routine r " +
            "JOIN RoutineDate rd ON r.id = rd.routine.id " +
            "WHERE r.member.id = :memberId AND rd.performedDate = :performedDate")
    Optional<Routine> findByMemberIdAndPerformedDate(@Param("memberId") Long memberId, @Param("performedDate") LocalDate performedDate);
}
