package com.pumping.domain.favorite.repository;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    void deleteByMemberAndBoard(@Param("member") Member member, @Param("board") Board board);

    boolean existsByMemberAndBoard(Member member, Board board);

}
