package com.pumping.domain.media.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.media.model.Media;
import com.pumping.domain.media.repository.MediaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @InjectMocks
    private MediaService mediaService;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private BoardRepository boardRepository;

    @Test
    void 파일을_성공적으로_저장한다() throws Exception {

        Long boardId = 1L;
        Board board = mock(Board.class);
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "이미지데이터".getBytes()
        );

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        mediaService.save(boardId, file);

        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());

        Media savedMedia = captor.getValue();
        assertThat(savedMedia.getBoard()).isEqualTo(board);
        assertThat(savedMedia.getFileName()).isEqualTo("image.jpg");
        assertThat(savedMedia.getFileType()).isEqualTo("image/jpeg");
        assertThat(savedMedia.getData()).isEqualTo("이미지데이터".getBytes());
    }

    @Test
    void 게시글이_존재하지_않으면_예외가_발생한다() {

        Long boardId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "img".getBytes());

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.save(boardId, file))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }

    @Test
    void 파일_읽기중_IOException이_발생하면_RuntimeException을_던진다() throws Exception {

        Long boardId = 1L;
        Board board = mock(Board.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(mockFile.getBytes()).thenThrow(new IOException("파일 실패"));

        assertThatThrownBy(() -> mediaService.save(boardId, mockFile))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void 이미지를_ID로_정상적으로_조회한다() {

        Long mediaId = 10L;
        byte[] data = "imageBytes".getBytes();
        Media media = mock(Media.class);
        when(media.getData()).thenReturn(data);
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        byte[] result = mediaService.findById(mediaId);

        assertThat(result).isEqualTo(data);
    }

    @Test
    void 이미지_ID가_존재하지_않으면_예외를_던진다() {

        Long mediaId = 10L;
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.findById(mediaId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("이미지를 찾을 수 없습니다");
    }
}