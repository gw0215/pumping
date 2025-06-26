package com.pumping.domain.comment.service;

import com.pumping.domain.board.exception.NoPermissionException;
import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.fixture.CommentFixture;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.comment.repository.CommentRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Test
    void 댓글을_저장하면_댓글_개수가_증가하고_저장이_된다() {

        Member member = MemberFixture.createMember();
        Long boardId = 1L;
        Board board = Mockito.mock(Board.class);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(commentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commentService.save(member, boardId, "댓글 내용");

        verify(board).plusCommentCount();
        verify(commentRepository).save(Mockito.any(Comment.class));
    }

    @Test
    void 게시글ID로_댓글목록을_조회한다() {
        Member member = MemberFixture.createMember();
        Board board = BoardFixture.createBoard(member);
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = List.of(CommentFixture.createComment(member, board));
        when(commentRepository.findByBoardId(1L, pageable))
                .thenReturn(new PageImpl<>(comments, pageable, comments.size()));

        Page<CommentResponse> result = commentService.findAll(1L, pageable);

        assertThat(result).hasSize(1);
        verify(commentRepository).findByBoardId(1L, pageable);
    }

    @Test
    void 게시글이_없으면_댓글_저장시_예외가_발생한다() {

        Long boardId = 1L;
        Member member = MemberFixture.createMember();

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.save(member, boardId, "댓글"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 본인_댓글을_수정하면_내용이_변경된다() {
        Member member = MemberFixture.createMemberWithId(1L);
        Comment comment = Mockito.mock(Comment.class);
        when(comment.getMember()).thenReturn(member);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.update(1L, "수정된 내용", member);

        verify(comment).updateContent("수정된 내용");
    }

    @Test
    void 존재하지_않는_댓글을_수정하면_예외가_발생한다() {
        Member member = MemberFixture.createMember();
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.update(1L, "수정내용", member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }

    @Test
    void 다른_사람의_댓글을_수정하면_예외가_발생한다() {
        Member writer = MemberFixture.createMemberWithId(1L);
        Member attacker = MemberFixture.createMemberWithId(2L);
        Comment comment = Mockito.mock(Comment.class);
        when(comment.getMember()).thenReturn(writer);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(1L, "내용", attacker))
                .isInstanceOf(NoPermissionException.class)
                .hasMessageContaining("권한이 없습니다");
    }


    @Test
    void 댓글이_없으면_댓글_삭제시_예외가_발생한다() {
        Member member = MemberFixture.createMember();
        Board board = Mockito.mock(Board.class);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(commentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.delete(1L, 2L, member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");
    }

    @Test
    void 댓글을_삭제하면_댓글_개수가_감소하고_삭제된다() {

        Long boardId = 1L;
        Long commentId = 5L;
        Member member = MemberFixture.createMemberWithId(1L);
        Board board = Mockito.mock(Board.class);
        Comment comment = CommentFixture.createComment(member, board);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(boardId, commentId, member);

        verify(board).minusCommentCount();
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void 게시글이_없으면_댓글_삭제시_예외가_발생한다() {

        Long boardId = 1L;
        Long commentId = 5L;
        Member member = MemberFixture.createMember();
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.delete(boardId, commentId, member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 다른_사람의_댓글을_삭제하면_예외가_발생한다() {
        Member writer = MemberFixture.createMemberWithId(1L);
        Member attacker = MemberFixture.createMemberWithId(2L);
        Board board = Mockito.mock(Board.class);
        Comment comment = Mockito.mock(Comment.class);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
        when(comment.getMember()).thenReturn(writer);

        assertThatThrownBy(() -> commentService.delete(1L, 2L, attacker))
                .isInstanceOf(NoPermissionException.class)
                .hasMessageContaining("권한이 없습니다");
    }
}