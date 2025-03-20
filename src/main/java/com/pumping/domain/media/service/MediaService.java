package com.pumping.domain.media.service;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.media.model.Media;
import com.pumping.domain.media.repository.MediaRepository;
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

        Board board = boardRepository.findById(boardId)
                .orElseThrow(RuntimeException::new);

        byte[] data = null;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Media media = new Media(board, file.getOriginalFilename(), file.getContentType(), data);
        mediaRepository.save(media);
    }

    @Transactional
    public Media findById(Long mediaId) {
        return mediaRepository.findById(mediaId).orElseThrow(RuntimeException::new);
    }
}
