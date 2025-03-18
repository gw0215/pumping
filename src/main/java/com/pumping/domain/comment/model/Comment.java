package com.pumping.domain.comment.model;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String comment;

    public Comment(Member member, Board board, String comment) {
        this.member = member;
        this.board = board;
        this.comment = comment;
    }
}
