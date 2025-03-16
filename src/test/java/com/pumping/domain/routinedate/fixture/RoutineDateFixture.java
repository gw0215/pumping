package com.pumping.domain.routinedate.fixture;

import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.routinedate.dto.RoutineDateRequest;
import com.pumping.domain.routinedate.model.RoutineDate;

import java.time.LocalDate;

public abstract class RoutineDateFixture {

    public static LocalDate DATE = LocalDate.now();

    public static RoutineDateRequest createRoutineDateRequest(Long routineId) {
        return new RoutineDateRequest(routineId, DATE);
    }

    public static RoutineDate createRoutineDate(Routine routine) {
        return new RoutineDate(routine, DATE);
    }

}
