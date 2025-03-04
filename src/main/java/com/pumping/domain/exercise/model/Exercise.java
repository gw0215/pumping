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

    private String explain;

    private String part;

    @OneToMany(mappedBy = "exercise")
    private List<RoutineExercise> routineExercise = new ArrayList<>();

    public Exercise(String name, String explain, String part) {
        this.name = name;
        this.explain = explain;
        this.part = part;
    }

    
}
