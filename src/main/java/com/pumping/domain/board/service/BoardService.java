package com.pumping.domain.board.service;

import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.exception.NoPermissionException;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.favorite.repository.FavoriteRepository;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long save(Member member, String title, String content, MultipartFile file) {

        Board board = new Board(member, title, content);

        if (file != null && !file.isEmpty()) {
            byte[] data;
            try {
                data = file.getBytes();
            } catch (IOException e) {
                throw new UncheckedIOException("파일을 읽는 중 오류 발생: " + file.getOriginalFilename(), e);
            }
            Media media = new Media(board, file.getOriginalFilename(), file.getContentType(), data);
            board.addMedia(media);
        }

        return boardRepository.save(board).getId();

    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> findAll(Member member, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findBoards(pageable);

        List<Long> boardIds = boardPage.getContent().stream()
                .map(Board::getId)
                .toList();

        List<Long> likedBoardIds = favoriteRepository.findBoardIdsByMemberAndBoardIds(member.getId(), boardIds);

        List<BoardResponse> boardResponses = boardPage.getContent().stream()
                .map(board -> BoardResponse.from(board, likedBoardIds.contains(board.getId())))
                .toList();

        return new PageImpl<>(boardResponses, pageable, boardPage.getTotalElements());
    }


    @Transactional
    public void update(Long boardId, String title, String content, Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        if (!board.getMember().getId().equals(member.getId())) {
            throw new NoPermissionException("해당 게시글을 수정할 권한이 없습니다.");
        }
        board.updateTitle(title);
        board.updateContent(content);
    }


    @Transactional
    public void deleteById(Long boardId,Member member) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. 게시글 ID : " + boardId));
        if (!board.getMember().getId().equals(member.getId())) {
            throw new NoPermissionException("해당 게시글을 삭제할 권한이 없습니다.");
        }
        board.deleteBoard();
    }

}
