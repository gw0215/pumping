package com.pumping.domain.routine.repository;

import com.pumping.domain.routine.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

}
