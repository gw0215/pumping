package com.pumping.domain.comment.service;

import com.pumping.domain.board.exception.NoPermissionException;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.comment.repository.CommentRepository;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    @Transactional
    public Long save(Member member, Long boardId, String content) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        board.plusCommentCount();
        Comment comment = new Comment(member, board, content);
        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> findAll(Long boardId, Pageable pageable) {

        Page<Comment> comments = commentRepository.findByBoardId(boardId,pageable);

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> new CommentResponse(comment.getId(), comment.getMember().getId(), comment.getMember().getNickname(), comment.getContent()))
                .toList();

        return new PageImpl<>(commentResponses,pageable,comments.getTotalElements());

    }

    @Transactional
    public void update(Long commentId, String content, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID : " + commentId));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new NoPermissionException("해당 댓글을 수정할 권한이 없습니다.");
        }
        comment.updateContent(content);
    }


    @Transactional
    public void delete(Long boardId, Long commentId, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. 댓글 ID : " + commentId));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new NoPermissionException("해당 댓글을 수정할 권한이 없습니다.");
        }
        board.minusCommentCount();
        commentRepository.deleteById(commentId);
    }

}
