package com.pumping.domain.routinedate.repository;

import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routinedate.model.RoutineDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface RoutineDateRepository extends JpaRepository<RoutineDate, Long> {

    @Query("SELECT rd FROM RoutineDate rd " +
            "JOIN Routine r ON r.id = rd.routine.id " +
            "WHERE r.member.id = :memberId AND rd.performedDate = :performedDate")
    Optional<RoutineDate> findByMemberIdAndPerformedDate(@Param("memberId") Long memberId, @Param("performedDate") LocalDate performedDate);

}
