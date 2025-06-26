package com.pumping.domain.routine.fixture;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.model.Routine;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RoutineFixture {

    public static Routine createRoutine(Member member, String routineName) {
        return new Routine(member, routineName);
    }

    public static Routine createRoutineWithId(Member member, String routineName,Long id) {
        Routine routine = new Routine(member, routineName);
        ReflectionTestUtils.setField(routine, "id", id);
        return routine;
    }

    public static List<Routine> createRoutines(Member member, String routineName, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createRoutine(member, routineName + i))
                .collect(Collectors.toList());
    }

}
