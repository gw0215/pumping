package com.pumping.domain.exercisehistory.repository;

import com.pumping.domain.exercisehistory.dto.ExercisePartSetCountDto;
import com.pumping.domain.exercisehistory.model.ExerciseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseHistoryRepository extends JpaRepository<ExerciseHistory, Long> {

    @Query("SELECT eh FROM ExerciseHistory eh WHERE eh.member.id = :memberId AND eh.performedDate = :performedDate")
    Optional<ExerciseHistory> findByMemberIdAndPerformedDate(@Param("memberId") Long memberId, @Param("performedDate") LocalDate performedDate);

    @Query(value = """
            SELECT DATE(eh.performed_date) AS performedDate,
                SUM(TIME_TO_SEC(eh.performed_time)) AS totalSeconds,
                SUM(CASE WHEN pes.completed = TRUE THEN pes.weight ELSE 0 END) AS totalVolume
            FROM exercise_history eh
            JOIN performed_exercise pe ON eh.id = pe.exercise_history_id
            JOIN performed_exercise_set pes ON pe.id = pes.performed_exercise_id
            WHERE eh.member_id = :memberId
              AND eh.exercise_history_status = 'COMPLETED'
              AND eh.performed_date BETWEEN :startDate AND :endDate
            GROUP BY eh.performed_date
            ORDER BY performedDate
            """, nativeQuery = true)
    List<WeeklyExerciseHistoryStatsDto> findWeeklyStats(@Param("memberId") Long memberId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("""
                SELECT new com.pumping.domain.exercisehistory.dto.ExercisePartSetCountDto(e.exercisePart, SUM(s.setCount))
                FROM PerformedExerciseSet s
                JOIN s.performedExercise pe
                JOIN pe.exercise e
                JOIN pe.exerciseHistory eh
                JOIN eh.member m
                WHERE s.completed = true
                  AND eh.performedDate BETWEEN :startDate AND :endDate
                  AND m.id = :memberId
                GROUP BY e.exercisePart
            """)
    List<ExercisePartSetCountDto> countSetPerExercisePart(@Param("memberId") Long memberId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT e.exercisePart AS part,
                    FUNCTION('MONTH', eh.performedDate) AS month,
                    SUM(pes.weight * pes.repetition * pes.setCount) AS totalVolume
                FROM ExerciseHistory eh
                JOIN eh.performedExercises pe
                JOIN pe.performedExerciseSets pes
                JOIN pe.exercise e
                WHERE eh.member.id = :memberId
                    AND eh.exerciseHistoryStatus = 'COMPLETED'
                    AND pes.completed = true
                    AND eh.performedDate BETWEEN :start AND :end
                GROUP BY  e.exercisePart, FUNCTION('MONTH', eh.performedDate)
                ORDER BY e.exercisePart, FUNCTION('MONTH', eh.performedDate)
            """)
    List<MonthlyPartVolumeDto> findMonthlyVolumeByPart(@Param("memberId") Long memberId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
