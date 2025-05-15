package com.pumping.domain.exercise.repository;

import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
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
class ExerciseRepositoryTest {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Test
    void 운동_부위_조회_테스트() {

        List<Exercise> exerciseList = ExerciseFixture.createExercises(5);
        exerciseRepository.saveAll(exerciseList);

        ExercisePart exercisePart = exerciseList.get(0).getExercisePart();
        List<Exercise> exercises = exerciseRepository.findAllByExercisePart(exercisePart);

        Assertions.assertThat(exercises).hasSize(5);
        exercises.forEach(exercise -> Assertions.assertThat(exercise.getExercisePart()).isEqualTo(exercisePart));
    }

}