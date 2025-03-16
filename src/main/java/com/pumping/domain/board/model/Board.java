package com.pumping.domain.board.model;

import com.pumping.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    public Member member;

    public String content;

    public Integer likeCount = 0;

    public Board(Member member, String content) {
        this.member = member;
        this.content = content;
    }
}
