package com.pumping.domain.comment.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.comment.repository.CommentRepository;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public List<CommentResponse> findAll(Long boardId) {
        return commentRepository.findByBoardId(boardId)
                .stream()
                .map(comment -> new CommentResponse(comment.getId(), comment.getMember().getNickname(), comment.getContent()))
                .toList();
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
