package com.pumping.domain.favorite.fixture;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.member.model.Member;

import java.util.List;

public abstract class FavoriteFixture {

    public static Favorite createFavorite(Member member, Board board) {
        return new Favorite(member, board);
    }

    public static List<Favorite> createFavorites(List<Member> members, Board board) {
        return members.stream()
                .map(member -> {
                    Favorite favorite = createFavorite(member, board);
                    board.addFavorite(favorite);
                    return favorite;
                })
                .toList();
    }

}
