package com.pumping.domain.board.model;

import com.pumping.domain.favorite.model.Favorite;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String title;

    public String content;

    public Integer likeCount = 0;

    public Integer commentCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    public Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Media> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Favorite> favoriteList = new ArrayList<>();

    private boolean deleted = false;

    public Board(Member member, String title, String content) {
        this.title = title;
        this.member = member;
        this.content = content;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void addMedia(Media media) {
        mediaList.add(media);
    }

    public void plusLikeCount() {
        this.likeCount++;
    }

    public void minusLikeCount() {
        this.likeCount--;
    }

    public void deleteBoard() {
        this.deleted = true;
    }

    public void plusCommentCount() {
        this.commentCount++;
    }

    public void minusCommentCount() {
        this.commentCount--;
    }
}
