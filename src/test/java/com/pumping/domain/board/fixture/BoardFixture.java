package com.pumping.domain.board.fixture;

import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.dto.BoardResponse;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.member.model.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.IntStream;

public abstract class BoardFixture {

    private static final String TITLE = "제목";

    private static final String CONTENT = "게시글 내용";

    public static BoardRequest createBoardRequest() {
        return new BoardRequest(TITLE, CONTENT);
    }

    public static BoardRequest createBoardRequest(String title, String content) {
        return new BoardRequest(title, content);
    }

    public static Board createBoard(Member member) {
        return new Board(member, TITLE, CONTENT);
    }

    public static Board createBoard(Member member, Long id) {
        Board board = new Board(member, "제목", "내용");
        ReflectionTestUtils.setField(board, "id", id);
        return board;
    }

    public static List<Board> createBoards(Member member, int count) {

        return IntStream.range(0, count)
                .mapToObj(i -> createBoard(member))
                .toList();
    }

    public static BoardResponse createBoardResponse(Long boardId) {
        return new BoardResponse(
                boardId,
                1L,
                "닉네임",
                "profile.png",
                "제목",
                "내용",
                10,
                5,
                List.of(),
                false
        );
    }

}
