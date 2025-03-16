package com.pumping.domain.board.fixture;

import com.pumping.domain.board.dto.BoardRequest;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.member.model.Member;

import java.util.List;
import java.util.stream.IntStream;

public abstract class BoardFixture {

    public static String CONTENT = "게시글 내용";

    public static BoardRequest createBoardRequest() {
        return new BoardRequest(CONTENT);
    }

    public static Board createBoard(Member member) {
        return new Board(member, CONTENT);
    }

    public static List<Board> createBoards(Member member, int count) {

        return IntStream.range(0, count)
                .mapToObj(i -> createBoard(member))
                .toList();
    }

}
