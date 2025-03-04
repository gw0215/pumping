package com.pumping.domain.exercise.repository;

import com.pumping.domain.exercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
