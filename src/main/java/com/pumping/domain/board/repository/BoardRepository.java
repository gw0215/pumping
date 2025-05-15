package com.pumping.domain.board.repository;

import com.pumping.domain.board.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.member.deleted = false AND b.deleted = false")
    Page<Board> findBoardsWithFavoritesByMember(Pageable pageable);
}
