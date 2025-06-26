package com.pumping.domain.exercisehistory.service;

import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ExerciseHistoryScheduler {

    private final ExerciseHistoryService exerciseHistoryService;
    private final MemberRepository memberRepository;
    private final PushNotificationService pushNotificationService;

    @Scheduled(cron = "0 0 1 * * SUN")
    public void scheduleWeeklyReportGeneration() {
        List<Member> members = memberRepository.findAll();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusDays(7);
        LocalDate endDate = now.minusDays(1);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Member member : members) {
            CompletableFuture<Void> future = exerciseHistoryService.generateAllWeeklyReports(member, startDate, endDate)
                    .thenAcceptAsync(report -> {
                        pushNotificationService.sendWeeklyReport(member);
                    });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}