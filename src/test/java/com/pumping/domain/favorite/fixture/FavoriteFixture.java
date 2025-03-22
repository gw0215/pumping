package com.pumping.domain.favorite.fixture;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.member.model.Member;

public abstract class FavoriteFixture {

    public static Favorite createFavorite(Member member, Board board) {
        return new Favorite(member, board);
    }

}
