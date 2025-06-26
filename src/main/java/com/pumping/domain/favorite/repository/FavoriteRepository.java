package com.pumping.domain.favorite.repository;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    void deleteByMemberAndBoard(Member member, Board board);

    @Query("SELECT f.board.id FROM Favorite f WHERE f.member.id = :memberId AND f.board.id IN :boardIds")
    List<Long> findBoardIdsByMemberAndBoardIds(@Param("memberId") Long memberId, @Param("boardIds") List<Long> boardIds);
}
