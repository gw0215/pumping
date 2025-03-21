package com.pumping.domain.favorite.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.favorite.repository.FavoriteRepository;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final BoardRepository boardRepository;

    private final FavoriteRepository favoriteRepository;

    @Transactional
    public void save(Member member, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(RuntimeException::new);
        board.plusLikeCount();
        Favorite favorite = new Favorite(member, board);
        favoriteRepository.save(favorite);

    }

    @Transactional
    public void delete(Member member, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(RuntimeException::new);
        board.minusLikeCount();
        favoriteRepository.deleteByMemberAndBoard(member, board);
    }

}
