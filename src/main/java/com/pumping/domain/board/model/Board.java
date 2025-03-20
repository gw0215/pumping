package com.pumping.domain.board.model;

import com.pumping.domain.media.model.Media;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board {

    @ManyToOne(fetch = FetchType.LAZY)
    public Member member;
    public String title;
    public String content;
    public Integer likeCount = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "board")
    private List<Media> mediaList = new ArrayList<>();

    public Board(Member member, String title, String content) {
        this.title = title;
        this.member = member;
        this.content = content;
    }
}
