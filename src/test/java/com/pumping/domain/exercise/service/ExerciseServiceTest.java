package com.pumping.domain.exercise.service;

import com.pumping.domain.exercise.dto.ExerciseResponse;
import com.pumping.domain.exercise.fixture.ExerciseFixture;
import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.model.ExercisePart;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @InjectMocks
    private ExerciseService exerciseService;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    void CHEST_부위로_요청시_해당_운동_리스트를_리턴한다() {
        Exercise exercise = ExerciseFixture.createExercise(ExercisePart.CHEST, "푸쉬업");

        when(exerciseRepository.findAllByExercisePart(ExercisePart.CHEST))
                .thenReturn(List.of(exercise));

        List<ExerciseResponse> result = exerciseService.findAll("CHEST");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("푸쉬업");
        assertThat(result.get(0).getPart()).isEqualTo(ExercisePart.CHEST.name());
    }

    @Test
    void 존재하지_않는_운동_부위로_요청시_예외를_던진다() {
        assertThatThrownBy(() -> exerciseService.findAll("INVALID_PART"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant");
    }

    @Test
    void part가_null일때_NullPointerException_발생한다() {
        assertThatThrownBy(() -> exerciseService.findAll(null))
                .isInstanceOf(NullPointerException.class);
    }
}