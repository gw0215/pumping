package com.pumping.domain.comment.fixture;

import com.pumping.domain.board.model.Board;
import com.pumping.domain.comment.dto.CommentRequest;
import com.pumping.domain.comment.dto.CommentResponse;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.member.model.Member;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public abstract class CommentFixture {

    private static String COMMENT = "comment";

    public static CommentRequest createCommentRequest() {
        return new CommentRequest(COMMENT);
    }

    public static Comment createComment(Member member, Board board) {
        return new Comment(member, board, COMMENT);
    }

    public static List<Comment> createComments(Member member, Board board, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createComment(member, board))
                .toList();
    }

    public static List<CommentResponse> createCommentResponse(int count) {
        return LongStream.range(0, count)
                .mapToObj(i -> new CommentResponse(
                        i + 1,
                        i + 1,
                        "작성자" + i,
                        "댓글 내용 " + i
                ))
                .toList();
    }

}
