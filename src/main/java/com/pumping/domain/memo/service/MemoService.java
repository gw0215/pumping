package com.pumping.domain.memo.service;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.exercise.repository.ExerciseRepository;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.memo.model.Memo;
import com.pumping.domain.memo.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final ExerciseRepository exerciseRepository;

    private final MemoRepository memoRepository;

    @Transactional
    public void save(Member member, Long exerciseId, String detail) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(RuntimeException::new);
        Memo memo = new Memo(member, exercise, detail);
        memoRepository.save(memo);
    }

}
