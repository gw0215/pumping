package com.pumping.domain.routine.repository;

import com.pumping.domain.routine.model.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    Page<Routine> findAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
