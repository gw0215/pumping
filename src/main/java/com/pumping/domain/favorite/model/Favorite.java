package com.pumping.domain.favorite.model;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public Favorite(Member member, Board board) {
        this.member = member;
        this.board = board;
    }
}
