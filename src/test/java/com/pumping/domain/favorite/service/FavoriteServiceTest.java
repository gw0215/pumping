package com.pumping.domain.favorite.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.favorite.model.Favorite;
import com.pumping.domain.favorite.repository.FavoriteRepository;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Test
    void save_좋아요_정상_저장() {
        Member member = MemberFixture.createMember();
        Board board = mock(Board.class);
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        favoriteService.save(member, boardId);

        verify(board).plusLikeCount();
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void save_게시글이_없으면_예외발생() {
        Member member = MemberFixture.createMember();
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.save(member, boardId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void delete_좋아요_정상_삭제() {
        Member member = MemberFixture.createMember();
        Board board = mock(Board.class);
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        favoriteService.delete(member, boardId);

        verify(board).minusLikeCount();
        verify(favoriteRepository).deleteByMemberAndBoard(member, board);
    }

    @Test
    void delete_게시글이_없으면_예외발생() {
        Member member = MemberFixture.createMember();
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.delete(member, boardId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }
}