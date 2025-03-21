package com.pumping.domain.board.service;

import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.favorite.repository.FavoriteRepository;
import com.pumping.domain.media.dto.MediaResponse;
import com.pumping.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long save(Member member, String title, String content) {
        Board board = new Board(member, title, content);
        return boardRepository.save(board).getId();
    }

    @Transactional
    public List<BoardResponse> findAll(Member member) {
        return boardRepository.findAll()
                .stream()
                .map(board -> {

                    List<MediaResponse> mediaResponses = board.getMediaList().stream()
                            .map(media -> new MediaResponse(media.getId(), media.getFileName(), media.getFileType()))
                            .toList();

                    boolean liked = favoriteRepository.existsByMemberAndBoard(member, board);

                    return new BoardResponse(board.getId(), board.member.getNickname(), board.getTitle(), board.getContent(), board.likeCount, mediaResponses, liked);
                })
                .toList();
    }

}
