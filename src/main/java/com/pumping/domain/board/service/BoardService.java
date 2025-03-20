package com.pumping.domain.board.service;

import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.media.dto.MediaResponse;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Long save(Member member, String title, String content) {
        Board board = new Board(member, title, content);
        return boardRepository.save(board).getId();
    }

    @Transactional
    public List<BoardResponse> findALl() {
        return boardRepository.findAll()
                .stream()
                .map(board -> {

                    List<MediaResponse> mediaResponses = board.getMediaList().stream()
                            .map(media -> new MediaResponse(media.getId(), media.getFileName(), media.getFileType()))
                            .toList();

                    return new BoardResponse(board.getId(), board.getTitle(), board.getContent(), board.likeCount, mediaResponses);
                })
                .toList();
    }

}
