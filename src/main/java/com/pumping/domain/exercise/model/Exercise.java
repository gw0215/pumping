package com.pumping.domain.exercise.model;

import com.pumping.domain.routineexercise.model.RoutineExercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String explanation;

    @Enumerated(EnumType.STRING)
    private ExercisePart exercisePart;

    @OneToMany(mappedBy = "exercise")
    private List<RoutineExercise> routineExercise = new ArrayList<>();

    public Exercise(String name, String explanation, ExercisePart exercisePart) {
        this.name = name;
        this.explanation = explanation;
        this.exercisePart = exercisePart;
    }

    
}
