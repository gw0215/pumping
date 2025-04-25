package com.pumping.domain.performedroutine.fixture;

import com.pumping.domain.performedroutine.model.PerformedRoutineStatus;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.performedroutine.dto.PerformedRoutineRequest;
import com.pumping.domain.performedroutine.model.PerformedRoutine;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class RoutineDateFixture {

    public static LocalDate DATE = LocalDate.now();

    public static LocalTime LOCAL_TIME = LocalTime.MIDNIGHT;

    public static PerformedRoutineRequest createRoutineDateRequest(Long routineId) {
        return new PerformedRoutineRequest(routineId, DATE);
    }

    public static PerformedRoutine createRoutineDate(Routine routine) {
        return new PerformedRoutine(routine, LOCAL_TIME, PerformedRoutineStatus.IN_PROGRESS, DATE);
    }

}
