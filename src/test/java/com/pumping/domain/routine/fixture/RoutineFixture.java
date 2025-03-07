package com.pumping.domain.routine.fixture;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.routine.model.Routine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class RoutineFixture {

    public static Routine createRoutine(Member member) {
        return new Routine(member, "name");
    }

    public static List<Routine> createRoutines(Member member, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createRoutine(member))
                .collect(Collectors.toList());
    }

}
