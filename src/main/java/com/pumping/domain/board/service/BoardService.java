package com.pumping.domain.board.service;

import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public void save(Member member, String content) {
        Board board = new Board(member, content);
        boardRepository.save(board);
    }

    @Transactional
    public List<BoardResponse> findALl() {
        return boardRepository.findAll()
                .stream()
                .map(board -> new BoardResponse(board.getId(), board.getContent(), board.likeCount))
                .toList();
    }

}
