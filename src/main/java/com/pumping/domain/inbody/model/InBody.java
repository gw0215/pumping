package com.pumping.domain.inbody.model;

import com.pumping.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class InBody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Float weight;

    private Float smm;

    private Float bfm;

    private LocalDate date;

    public InBody(Member member, Float weight, Float smm, Float bfm, LocalDate date) {
        this.member = member;
        this.weight = weight;
        this.smm = smm;
        this.bfm = bfm;
        this.date = date;
    }

    public void updateWeight(Float weight) {
        this.weight = weight;
    }

    public void updateSmm(Float smm) {
        this.smm = smm;
    }

    public void updateBfm(Float bfm) {
        this.bfm = bfm;
    }

}
