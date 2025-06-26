package com.pumping.domain.comment.repository;

import com.pumping.domain.board.fixture.BoardFixture;
import com.pumping.domain.board.model.Board;
import com.pumping.domain.board.repository.BoardRepository;
import com.pumping.domain.comment.fixture.CommentFixture;
import com.pumping.domain.comment.model.Comment;
import com.pumping.domain.member.fixture.MemberFixture;
import com.pumping.domain.member.model.Member;
import com.pumping.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Test
    void 게시글_ID로_댓글_조회_테스트() {
        Member member = memberRepository.save(MemberFixture.createMember());
        Board board = BoardFixture.createBoard(member);
        boardRepository.save(board);

        List<Comment> comments = CommentFixture.createComments(member, board, 3);
        commentRepository.saveAll(comments);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Comment> commentPage = commentRepository.findByBoardId(board.getId(), pageable);
        List<Comment> commentList = commentPage.getContent();
        Assertions.assertThat(commentList).hasSize(3);
        Assertions.assertThat(commentList).allMatch(comment -> comment.getBoard().getId().equals(board.getId()));
    }


}