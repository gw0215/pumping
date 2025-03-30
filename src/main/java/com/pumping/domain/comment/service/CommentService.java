package com.pumping.domain.comment.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.comment.repository.CommentRepository;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    @Transactional
    public void save(Member member, Long boardId, String content) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        board.plusCommentCount();
        Comment comment = new Comment(member, board, content);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findAll(Long boardId) {

        List<Comment> comments = commentRepository.findByBoardId(boardId);

        List<CommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {
            commentResponses.add(new CommentResponse(comment.getId(), comment.getMember().getId(), comment.getMember().getNickname(), comment.getContent()));
        }

        return commentResponses;
    }

    @Transactional
    public void update(Long commentId,String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. 게시글 ID : " + commentId));
        comment.updateContent(content);
    }


    @Transactional
    public void delete(Long boardId, Long commentId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        board.minusCommentCount();
        commentRepository.deleteById(commentId);
    }

}
