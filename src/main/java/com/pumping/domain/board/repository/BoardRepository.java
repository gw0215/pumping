package com.pumping.domain.board.repository;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN Favorite f on f.board = b AND f.member = :member " +
            "WHERE b.member.deleted = false")
    Page<Board> findBoardsWithFavoritesByMember(@Param("member") Member member, Pageable pageable);

}
