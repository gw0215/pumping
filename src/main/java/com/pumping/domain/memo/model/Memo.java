package com.pumping.domain.memo.model;

import com.pumping.domain.exercise.model.Exercise;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exercise exercise;

    private String detail;

    public Memo(Member member, Exercise exercise, String detail) {
        this.member = member;
        this.exercise = exercise;
        this.detail = detail;
    }
}
