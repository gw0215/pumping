package com.pumping.domain.board.service;

import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.media.dto.MediaResponse;
import com.pumping.domain.media.model.Media;
import com.pumping.domain.member.model.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public void save(Member member, String title, String content, MultipartFile file) {

        Board board = new Board(member, title, content);

        if (file != null && !file.isEmpty()) {
            byte[] data;
            try {
                data = file.getBytes();
            } catch (IOException e) {
                throw new UncheckedIOException("파일을 읽는 중 오류 발생: " + file.getOriginalFilename(),e);
            }
            Media media = new Media(board, file.getOriginalFilename(), file.getContentType(), data);
            board.addMedia(media);
        }

        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> findAll(Member member, Pageable pageable) {

        Page<Board> boardPage = boardRepository.findBoardsWithFavoritesByMember(member, pageable);

        List<BoardResponse> boardResponses = new ArrayList<>();

        for (Board board : boardPage.getContent()) {
            List<MediaResponse> mediaResponses = new ArrayList<>();

            for (Media media : board.getMediaList()) {
                mediaResponses.add(new MediaResponse(media.getId(), media.getFileName(), media.getFileType()));
            }

            boolean liked = !board.getFavoriteList().isEmpty();

            boardResponses.add(new BoardResponse(board.getId(), board.member.getId(), board.member.getNickname(),board.member.getProfileImagePath(), board.getTitle(), board.getContent(), board.likeCount, board.commentCount, mediaResponses, liked));
        }

        return new PageImpl<>(boardResponses, pageable, boardPage.getTotalElements());
    }


    @Transactional
    public void update(Member member,Long boardId, String title, String content) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        board.updateTitle(title);
        board.updateContent(content);
    }


    @Transactional
    public void deleteById(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        board.deleteBoard();
    }

}
