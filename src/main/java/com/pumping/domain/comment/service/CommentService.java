package com.pumping.domain.comment.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.comment.repository.CommentRepository;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    @Transactional
    public void save(Member member, Long boardId, String comment) {

        Board board = boardRepository.findById(boardId).orElseThrow(RuntimeException::new);
        Comment comment1 = new Comment(member, board, comment);
        commentRepository.save(comment1);

    }

}
