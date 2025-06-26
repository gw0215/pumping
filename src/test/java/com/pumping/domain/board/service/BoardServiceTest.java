package com.pumping.domain.board.service;

import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.exception.NoPermissionException;
import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.favorite.repository.FavoriteRepository;
import com.pumping.domain.media.model.Media;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMemberWithId(1L);
    }

    @Test
    void save_파일없으면_Board만_저장된다() {

        String title = "제목";
        String content = "내용";
        MockMultipartFile file = null;

        Board board = BoardFixture.createBoard(member, 1L);

        when(boardRepository.save(any(Board.class))).thenReturn(board);

        boardService.save(member, title, content, file);

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(captor.capture());

        Board savedBoard = captor.getValue();
        assertThat(savedBoard.getTitle()).isEqualTo(title);
        assertThat(savedBoard.getMediaList()).isEmpty();
    }

    @Test
    void  save_파일있으면_Media가_추가된다() {
        String title = "제목";
        String content = "내용";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "file-content".getBytes()
        );

        Board board = BoardFixture.createBoard(member, 1L);
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        boardService.save(member, title, content, file);

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(captor.capture());

        Board savedBoard = captor.getValue();
        assertThat(savedBoard.getMediaList()).hasSize(1);
        Media media = savedBoard.getMediaList().get(0);
        assertThat(media.getFileName()).isEqualTo("test.jpg");
        assertThat(media.getFileType()).isEqualTo("image/jpeg");
    }

    @Test
    void save_파일읽기중_IOException_발생시_UncheckedIOException_던진다() throws IOException {
        String title = "제목";
        String content = "내용";

        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getBytes()).thenThrow(new IOException("읽기 실패"));
        when(mockFile.getOriginalFilename()).thenReturn("badfile.txt");

        assertThrows(UncheckedIOException.class, () -> boardService.save(member, title, content, mockFile));
    }

    @Test
    void findAll_정상조회_좋아요_포함() {
        Pageable pageable = PageRequest.of(0, 10);

        Board board1 = BoardFixture.createBoard(member, 101L);
        Board board2 = BoardFixture.createBoard(member, 102L);
        List<Board> boardList = List.of(board1, board2);

        Page<Board> boardPage = new PageImpl<>(boardList, pageable, boardList.size());

        when(boardRepository.findBoards(pageable)).thenReturn(boardPage);

        when(favoriteRepository.findBoardIdsByMemberAndBoardIds(ArgumentMatchers.eq(member.getId()), ArgumentMatchers.anyList()))
                .thenReturn(List.of(101L));

        Page<BoardResponse> result = boardService.findAll(member, pageable);

        assertThat(result.getContent()).hasSize(2);

        BoardResponse response1 = result.getContent().get(0);
        BoardResponse response2 = result.getContent().get(1);

        assertThat(response1.boardId()).isEqualTo(101L);
        assertThat(response1.liked()).isTrue();

        assertThat(response2.boardId()).isEqualTo(102L);
        assertThat(response2.liked()).isFalse();
    }

    @Test
    void update_정상적으로_제목과_내용을_수정한다() {

        Long boardId = 1L;
        String newTitle = "수정된 제목";
        String newContent = "수정된 내용";

        Board board = BoardFixture.createBoard(member, boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        boardService.update(boardId, newTitle, newContent,member);

        assertThat(board.getTitle()).isEqualTo(newTitle);
        assertThat(board.getContent()).isEqualTo(newContent);

        verify(boardRepository).findById(boardId);

    }

    @Test
    void update_게시글이_존재하지_않으면_예외를_던진다() {

        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.update(boardId, "title", "content",member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(boardRepository).findById(boardId);
    }

    @Test
    void update_작성자가_아니면_예외를_던진다() {
        Member other = MemberFixture.createMemberWithId(999L);

        Long boardId = 1L;
        Board board = BoardFixture.createBoard(member, boardId);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.update(boardId, "수정된 제목", "수정된 내용", other))
                .isInstanceOf(NoPermissionException.class)
                .hasMessage("해당 게시글을 수정할 권한이 없습니다.");

        verify(boardRepository).findById(boardId);
    }

    @Test
    void deleteById_정상적으로_소프트_삭제된다() {

        Long boardId = 1L;
        Board board = BoardFixture.createBoard(member, boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        boardService.deleteById(boardId,member);

        assertThat(board.isDeleted()).isTrue();

        verify(boardRepository).findById(boardId);
    }

    @Test
    void deleteById_게시글이_없으면_예외를_던진다() {

        Long boardId = 1L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.deleteById(boardId, member))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다. 게시글 ID : 1");

        verify(boardRepository).findById(boardId);
    }

    @Test
    void deleteById_작성자가_아니면_예외를_던진다() {

        Member other = MemberFixture.createMemberWithId(999L);

        Long boardId = 1L;
        Board board = BoardFixture.createBoard(member, boardId);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.deleteById(1L, other))
                .isInstanceOf(NoPermissionException.class)
                .hasMessage("해당 게시글을 삭제할 권한이 없습니다.");

        verify(boardRepository).findById(board.getId());
    }
}