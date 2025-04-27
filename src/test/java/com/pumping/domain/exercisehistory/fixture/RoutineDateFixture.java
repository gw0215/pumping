package com.pumping.domain.exercisehistory.fixture;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.exercisehistory.model.ExerciseHistoryStatus;
import com.pumping.domain.routine.model.Routine;
import com.pumping.domain.exercisehistory.dto.ExerciseHistoryRequest;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class RoutineDateFixture {

    public static LocalDate DATE = LocalDate.now();

    public static LocalTime LOCAL_TIME = LocalTime.MIDNIGHT;

    public static ExerciseHistoryRequest createRoutineDateRequest(Long routineId) {
        return new ExerciseHistoryRequest(routineId, DATE);
    }

    public static ExerciseHistory createRoutineDate(Member member, Routine routine) {
        return new ExerciseHistory(member, routine, LOCAL_TIME, ExerciseHistoryStatus.IN_PROGRESS, DATE);
    }

}
