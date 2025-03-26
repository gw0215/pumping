package com.pumping.domain.media.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.media.model.Media;
import com.pumping.domain.media.repository.MediaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    private final BoardRepository boardRepository;

    @Transactional
    public void save(Long boardId, MultipartFile file) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Media media = new Media(board, file.getOriginalFilename(), file.getContentType(), data);
        mediaRepository.save(media);
    }

    @Transactional
    public byte[] findById(Long mediaId) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다. 이미지 ID : " + mediaId));
        return media.getData();
    }
}
